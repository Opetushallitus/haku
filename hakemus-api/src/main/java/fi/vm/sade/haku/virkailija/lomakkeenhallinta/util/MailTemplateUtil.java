package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import fi.vm.sade.haku.oppija.common.oppijantunnistus.OppijanTunnistusDTO.LanguageCodeISO6391;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.SafeString;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Optional.fromNullable;
import static fi.vm.sade.haku.oppija.common.oppijantunnistus.OppijanTunnistusDTO.LanguageCodeISO6391.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE;
import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.SHORT;

public final class MailTemplateUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(MailTemplateUtil.class);

    private static final String PLACEHOLDER_LINK = "verification-link";
    private static final String PLACEHOLDER_LINK_EXPIRATION_TIME = "expires";

    private static final ImmutableMap<LanguageCodeISO6391, SafeString> emailSubjectTranslations = ImmutableMap.of(
            en, SafeString.of("Studyinfo - payment link"),
            fi, SafeString.of("Opintopolku - maksulinkki"),
            sv, SafeString.of("Studieinfo - betalningslänk"));

    private static final ImmutableMap<String, LanguageCodeISO6391> applicationLanguageToLanguageCodeMap = ImmutableMap.of(
            "suomi", fi,
            "ruotsi", sv,
            "englanti", en
    );

    private static LanguageCodeISO6391 languageCodeFromApplication(Application application) {
        SafeString language = SafeString.of(application.getPhaseAnswers(OppijaConstants.PHASE_MISC).get(ELEMENT_ID_CONTACT_LANGUAGE));
        return fromNullable(applicationLanguageToLanguageCodeMap.get(language.getValue())).or(en);
    }

    private static String c(String s) {
        return "{{" + s + "}}";
    }

    private static final Ordering<Date> dateComparator = new Ordering<Date>() {
        @Override
        public int compare(Date left, Date right) {
            return left.compareTo(right);
        }
    };

    private final static Function<ApplicationPeriod, Date> periodEnd = new Function<ApplicationPeriod, Date>() {
        @Override
        public Date apply(ApplicationPeriod applicationPeriod) {
            return applicationPeriod.getEnd();
        }
    };

    private static boolean isSane(Date d) {
        return d.getTime() < Long.MAX_VALUE;
    }

    private static Date nextEndDate(List<ApplicationPeriod> applicationPeriods, final Date changeTime) {
        return dateComparator.min(Iterables.transform(Iterables.filter(applicationPeriods, new Predicate<ApplicationPeriod>() {
            @Override
            public boolean apply(ApplicationPeriod applicationPeriod) {
                final Date end = applicationPeriod.getEnd();
                return isSane(end) && end.after(changeTime);
            }
        }), periodEnd));
    }

    private static Date withGracePeriod(Date changeTime, long gracePeriod) {
        return new DateTime(changeTime.getTime(), DateTimeZone.UTC).plus(gracePeriod).toDateMidnight().toDate();
    }

    private static boolean isWithinApplicationPeriods(List<ApplicationPeriod> applicationPeriods, final Date changeTime) {
        return Iterables.any(applicationPeriods, new Predicate<ApplicationPeriod>() {
            @Override
            public boolean apply(ApplicationPeriod period) {
                final Date start = period.getStart();
                final Date end = period.getEnd();

                return start.before(changeTime) && isSane(end) && end.after(changeTime);
            }
        });
    }

    public static Date calculateDueDate(List<ApplicationPeriod> applicationPeriods, final Date changeTime, long gracePeriod) {
        if (isWithinApplicationPeriods(applicationPeriods, changeTime)) {
            return dateComparator.max(nextEndDate(applicationPeriods, changeTime), withGracePeriod(changeTime, gracePeriod));
        } else {
            return withGracePeriod(changeTime, gracePeriod);
        }
    }

    private static SafeString createEmailTemplate(LanguageCodeISO6391 language, SafeString subject, Date dueDate) throws IOException {
        ImmutableMap<String, String> templateValues = ImmutableMap.of(
                "subject", subject.getValue(),
                "due_date", localizedDateString(dueDate, language),
                // Leave intact for receiver to fill
                PLACEHOLDER_LINK_EXPIRATION_TIME, c(PLACEHOLDER_LINK_EXPIRATION_TIME),
                PLACEHOLDER_LINK, c(PLACEHOLDER_LINK));

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/email/maksulinkki_" + language + ".mustache");
        StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, templateValues).flush();
        return SafeString.of(stringWriter.toString());
    }

    public static String localizedDateString(Date dueDate, LanguageCodeISO6391 language) {
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.forLanguageTag(language.toString()));
        return dateTimeInstance.format(dueDate);
    }

    public static Function<Application, PaymentEmail> paymentEmailFromApplication(final List<ApplicationPeriod> applicationPeriods) {
        return new Function<Application, PaymentEmail>() {
            @Override
            public PaymentEmail apply(Application application) {
                LanguageCodeISO6391 language = languageCodeFromApplication(application);
                SafeString subject = fromNullable(emailSubjectTranslations.get(language)).or(emailSubjectTranslations.get(en));
                try {
                    Date dueDate = application.getPaymentDueDate();
                    if (dueDate == null) {
                        throw new RuntimeException("paymentDueDate is missing");
                    }
                    return new PaymentEmail(subject, createEmailTemplate(language, subject, dueDate), language, dueDate);
                } catch (IOException e) {
                    LOGGER.error("Failed to create payment email from application " + application.getOid(), e);
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static DateFormat dateTimeFormatter(Locale locale) {
        return DateFormat.getDateTimeInstance(MEDIUM, SHORT, locale);
    }

    public static String getTextOrEmpty(I18nText text, Locale locale) {
        if (text == null) {
            return "";
        }
        return text.getText(locale.getLanguage());
    }
}
