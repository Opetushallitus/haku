package fi.vm.sade.haku.oppija.postprocess;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.HakumaksuService.PaymentEmail;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.LanguageCodeISO6391;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.Types.SafeString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import static com.google.common.base.Optional.fromNullable;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil.LanguageCodeISO6391.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE;

public final class MailTemplateUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(MailTemplateUtil.class);

    private static final String PLACEHOLDER_LINK = "verification-link";
    private static final String PLACEHOLDER_LINK_EXPIRATION_TIME = "expires";

    private static final DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

    static {
        iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static final ImmutableMap<LanguageCodeISO6391, SafeString> emailSubjectTranslations = ImmutableMap.of(
            en, SafeString.of("Studyinfo – maksulinkki"),
            fi, SafeString.of("Opintopolku - maksulinkki"),
            sv, SafeString.of("Studieinfo – maksulinkki"));

    private static final ImmutableMap<String, LanguageCodeISO6391> applicationLanguageToLanguageCodeMap = ImmutableMap.of(
            "suomi", fi,
            "ruotsi", sv,
            "englanti", en
    );

    public static <K, V> V getOrGet(Map<K, V> map, K key, K defaultKey) {
        return fromNullable(map.get(key)).or(map.get(defaultKey));
    }

    public static <K, V> V getOrValue(Map<K, V> map, K key, V defaultValue) {
        return fromNullable(map.get(key)).or(defaultValue);
    }

    private static LanguageCodeISO6391 languageCodeFromApplication(Application application) {
        SafeString language = SafeString.of(application.getPhaseAnswers(OppijaConstants.PHASE_MISC).get(ELEMENT_ID_CONTACT_LANGUAGE));
        return getOrValue(applicationLanguageToLanguageCodeMap, language.getValue(), en);
    }

    public static String iso8601Time() {
        return iso8601Time(new Date());
    }

    public static String iso8601Time(Date date) {
        return iso8601Format.format(date);
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

    private static Date applicationSystemLastClosingDate(ApplicationSystem applicationSystem) {
        return dateComparator.max(Iterables.transform(applicationSystem.getApplicationPeriods(), new Function<ApplicationPeriod, Date>() {
            @Override
            public Date apply(ApplicationPeriod applicationPeriod) {
                return applicationPeriod.getEnd();
            }
        }));
    }

    private static SafeString createEmailTemplate(LanguageCodeISO6391 language, SafeString subject, ApplicationSystem applicationSystem) throws IOException {
        ImmutableMap<String, String> templateValues = ImmutableMap.of(
                "subject", subject.getValue(),
                "submit_time", iso8601Time(),
                "due_date", iso8601Time(applicationSystemLastClosingDate(applicationSystem)),
                // Leave intact for receiver to fill
                PLACEHOLDER_LINK_EXPIRATION_TIME, c(PLACEHOLDER_LINK_EXPIRATION_TIME),
                PLACEHOLDER_LINK, c(PLACEHOLDER_LINK));

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/email/maksulinkki_" + language + ".mustache");
        StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, templateValues).flush();
        return SafeString.of(stringWriter.toString());
    }

    public static Function<Application, PaymentEmail> paymentEmailFromApplication(final ApplicationSystem applicationSystem) {
        return new Function<Application, PaymentEmail>() {
            @Override
            public PaymentEmail apply(Application application) {
                LanguageCodeISO6391 language = languageCodeFromApplication(application);
                SafeString subject = getOrGet(emailSubjectTranslations, language, en);
                try {
                    return new PaymentEmail(subject, createEmailTemplate(language, subject, applicationSystem), language);
                } catch (IOException e) {
                    LOGGER.error("Failed to create payment email from application " + application.getOid(), e);
                    return null;
                }
            }
        };
    }
}
