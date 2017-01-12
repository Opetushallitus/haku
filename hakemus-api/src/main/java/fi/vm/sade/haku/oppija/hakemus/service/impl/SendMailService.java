package fi.vm.sade.haku.oppija.hakemus.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import fi.vm.sade.haku.http.HttpRestClient;
import fi.vm.sade.haku.http.RestClient;
import fi.vm.sade.haku.oppija.common.oppijantunnistus.OppijanTunnistusDTO;
import fi.vm.sade.haku.oppija.common.oppijantunnistus.OppijanTunnistusDTO.LanguageCodeISO6391;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationAttachment;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationAttachmentRequest;
import fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.virkailija.viestintapalvelu.EmailService;
import fi.vm.sade.properties.OphProperties;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailMessage;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailRecipient;
import org.apache.commons.mail.EmailException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static fi.vm.sade.haku.oppija.hakemus.service.impl.SendMailService.EducationDegree.HIGHER;
import static fi.vm.sade.haku.oppija.hakemus.service.impl.SendMailService.EducationDegree.SECONDARY;
import static fi.vm.sade.haku.oppija.hakemus.service.impl.SendMailService.TemplateType.*;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters.isHuoltajanTiedotKysyttava;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.valmis.ValmisPhase.MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.MailTemplateUtil.dateTimeFormatter;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.MailTemplateUtil.getTextOrEmpty;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.*;
import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.Validate.notNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service(value = "sendMailService")
@Profile({"default", "devluokka"})
public class SendMailService {

    public static final Locale FI = new Locale("fi");
    public static final Locale SV = new Locale("sv");
    public static final Locale EN = new Locale("en");
    private final ApplicationSystemService applicationSystemService;

    final private Map<TemplateKey, Template> templateMap = new HashMap<>();

    public static final String TRUE = "true";
    private final OphProperties urlConfiguration;

    @Value("${mode.demo:false}")
    public boolean demoMode;

    @Value("${email.replyTo:noreply@opintopolku.fi}")
    String emailFrom;

    final RestClient restClient;

    final EmailService emailService;

    @Autowired
    public SendMailService(final ApplicationSystemService applicationSystemService,
                           final RestClient restClient,
                           final EmailService emailService, OphProperties urlConfiguration) {
        this.applicationSystemService = applicationSystemService;
        this.restClient = restClient;
        this.emailService = emailService;
        this.urlConfiguration = urlConfiguration;
        initTemplateMaps();
    }

    private void initTemplateMaps(){
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(VelocityEngine.INPUT_ENCODING, "UTF-8");
        velocityEngine.setProperty(VelocityEngine.OUTPUT_ENCODING, "UTF-8");
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.setProperty("class.resource.loader.path", "email");
        velocityEngine.setProperty("class.resource.loader.cache", "true");
        velocityEngine.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        velocityEngine.init();

        templateMap.put(new TemplateKey(FI, SECONDARY, RECEIVED), velocityEngine.getTemplate("email/application_received_fi.vm", "UTF-8"));
        templateMap.put(new TemplateKey(SV, SECONDARY, RECEIVED), velocityEngine.getTemplate("email/application_received_sv.vm", "UTF-8"));
        templateMap.put(new TemplateKey(EN, SECONDARY, RECEIVED), velocityEngine.getTemplate("email/application_received_en.vm", "UTF-8"));
        templateMap.put(new TemplateKey(FI, SECONDARY, RECEIVED_HUOLTAJA), velocityEngine.getTemplate("email/application_received_fi.vm", "UTF-8"));
        templateMap.put(new TemplateKey(SV, SECONDARY, RECEIVED_HUOLTAJA), velocityEngine.getTemplate("email/application_received_sv.vm", "UTF-8"));
        templateMap.put(new TemplateKey(EN, SECONDARY, RECEIVED_HUOLTAJA), velocityEngine.getTemplate("email/application_received_en.vm", "UTF-8"));
        templateMap.put(new TemplateKey(FI, HIGHER, RECEIVED), velocityEngine.getTemplate("email/application_received_higher_ed_fi.vm", "UTF-8"));
        templateMap.put(new TemplateKey(SV, HIGHER, RECEIVED), velocityEngine.getTemplate("email/application_received_higher_ed_sv.vm", "UTF-8"));
        templateMap.put(new TemplateKey(EN, HIGHER, RECEIVED), velocityEngine.getTemplate("email/application_received_higher_ed_en.vm", "UTF-8"));
        templateMap.put(new TemplateKey(FI, SECONDARY, MODIFIED), velocityEngine.getTemplate("email/application_modified_fi.vm", "UTF-8"));
        templateMap.put(new TemplateKey(SV, SECONDARY, MODIFIED), velocityEngine.getTemplate("email/application_modified_sv.vm", "UTF-8"));
        templateMap.put(new TemplateKey(EN, SECONDARY, MODIFIED), velocityEngine.getTemplate("email/application_modified_en.vm", "UTF-8"));
        templateMap.put(new TemplateKey(FI, SECONDARY, MODIFIED_HUOLTAJA), velocityEngine.getTemplate("email/application_modified_huoltaja_fi.vm", "UTF-8"));
        templateMap.put(new TemplateKey(SV, SECONDARY, MODIFIED_HUOLTAJA), velocityEngine.getTemplate("email/application_modified_huoltaja_sv.vm", "UTF-8"));
        templateMap.put(new TemplateKey(EN, SECONDARY, MODIFIED_HUOLTAJA), velocityEngine.getTemplate("email/application_modified_huoltaja_en.vm", "UTF-8"));
        templateMap.put(new TemplateKey(FI, HIGHER, MODIFIED), velocityEngine.getTemplate("email/application_modified_higher_ed_fi.vm", "UTF-8"));
        templateMap.put(new TemplateKey(SV, HIGHER, MODIFIED), velocityEngine.getTemplate("email/application_modified_higher_ed_sv.vm", "UTF-8"));
        templateMap.put(new TemplateKey(EN, HIGHER, MODIFIED), velocityEngine.getTemplate("email/application_modified_higher_ed_en.vm", "UTF-8"));
    }

    public void sendReceivedEmail(Application application) throws EmailException {
        if (!demoMode) {
            String email = application.getEmail();
            if (!isEmpty(email)) {
                sendEmail(application, email, RECEIVED, false);
            }
            String huoltajaEmail = application.getHuoltajaEmail();
            if (!isEmpty(huoltajaEmail) && isHuoltajanTiedotKysyttava(applicationSystemService.getApplicationSystem(application.getApplicationSystemId()))) {
                sendEmail(application, huoltajaEmail, RECEIVED_HUOLTAJA, true);
            }
        }
    }

    public void sendModifiedEmail(Application application) throws EmailException {
        if (!demoMode) {
            String email = application.getEmail();
            if (!isEmpty(email)) {
                sendEmail(application, email, MODIFIED, false);
            }

            String huoltajaEmail = application.getHuoltajaEmail();
            if (!isEmpty(huoltajaEmail) && isHuoltajanTiedotKysyttava(applicationSystemService.getApplicationSystem(application.getApplicationSystemId()))) {
                sendEmail(application, huoltajaEmail, MODIFIED_HUOLTAJA, false);
            }
        }
    }

    private void sendEmail(final Application application, final String emailAddress, final TemplateType type, final boolean forceNoSecureLink) throws EmailException {
        final ApplicationSystem as = applicationSystemService.getApplicationSystem(application.getApplicationSystemId());

        Locale locale = getLocale(application);
        ResourceBundle messages = ResourceBundle.getBundle("messages", locale);
        Template tmpl = selectTemplate(locale, as, type);
        final String emailSubject = getSubject(messages, type);
        StringWriter sw = new StringWriter();
        VelocityContext ctx = buildContext(application, as, locale, messages);
        tmpl.merge(ctx, sw);
        final String emailTemplate = sw.toString();

        if (forceNoSecureLink || doesNotUseSecurelink(as)) {
            sendNonSecurelinkEmail(emailAddress, emailSubject, emailTemplate);
        } else {
            sendSecurelinkEmail(application, as, emailAddress, emailSubject, emailTemplate, LanguageCodeISO6391.valueOf(locale.getLanguage()));
        }
    }

    // Send e-mail that doesn't contain e-mail link for modifying application option preferences
    private void sendNonSecurelinkEmail(final String emailAddress, final String emailSubject,
                                        final String emailTemplate) throws EmailException {
        EmailRecipient recipient = new EmailRecipient(){{
            setEmail(emailAddress);
        }};
        EmailMessage message = new EmailMessage() {{
            setCallingProcess("HAKUAPP");
            setSubject(emailSubject);
            setFrom(emailFrom);
            setHtml(true);
            setCharset("utf-8");
            setBody(emailTemplate);
        }};
        try {
            emailService.sendEmail(new EmailData(Lists.newArrayList(recipient), message));
        } catch (Exception e) {
            throw new EmailException("Sähköpostin lähettäminen epäonnistui", e);
        }
    }

    // Send e-mail that contains a "secure" link for modifying application option preferences
    private void sendSecurelinkEmail(final Application application, final ApplicationSystem as, final String emailAddress,
                                     final String emailSubject, final String emailTemplate,
                                     final LanguageCodeISO6391 emailLang) throws EmailException {
        OppijanTunnistusDTO body = new OppijanTunnistusDTO() {{
            this.url = urlConfiguration.url("omatsivut.email.application.modify.link." + emailLang.toString());
            this.expires = getModificationLinkExpiration(as);
            this.email = emailAddress;
            this.subject = emailSubject;
            this.template = emailTemplate;
            this.lang = emailLang;
            this.metadata = new Metadata() {{
                this.hakemusOid = application.getOid();
            }};
        }};

        try {
            boolean successStatusCode = Futures.transform(restClient.post(urlConfiguration.url("oppijan-tunnistus.create"), body, Object.class), new Function<HttpRestClient.Response<Object>, Boolean>() {
                @Override
                public Boolean apply(HttpRestClient.Response<Object> input) {
                    return input.isSuccessStatusCode();
                }
            }).get();
            if (!successStatusCode) {
                throw new EmailException("Sähköpostin lähettäminen epäonnistui");
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new EmailException("Sähköpostin lähettäminen epäonnistui", e);
        }
    }

    private String getSubject(ResourceBundle messages, TemplateType type) {
        return messages.getString(type.subjectKey);
    }

    private Template selectTemplate(Locale locale, ApplicationSystem applicationSystem, TemplateType type) {
        return templateMap.get(new TemplateKey(locale, applicationSystem.isHigherEducation() ? HIGHER : SECONDARY, type));
    }

    private static Locale getLocale(Application application) {
        String lang = application.getVastauksetMerged().get(ELEMENT_ID_CONTACT_LANGUAGE);
        Locale locale = FI;
        if ("ruotsi".equals(lang)) {
            locale = SV;
        } else if ("englanti".equals(lang)) {
            locale = EN;
        }
        return locale;
    }

    private long getModificationLinkExpiration(ApplicationSystem as) {
        return ApplicationSystem.getLastApplicationPeriodEnd(as.getApplicationPeriods()).getTime();
    }

    private VelocityContext buildContext(Application application, ApplicationSystem applicationSystem, Locale locale, ResourceBundle resourceBundle) {
        VelocityContext ctx = new VelocityContext();
        DateFormat dateFmt = dateTimeFormatter(locale);
        String receivedDate = dateFmt.format(application.getReceived());
        String modifiedDate = dateFmt.format(application.getUpdated() != null ? application.getUpdated() : application.getReceived());
        String applicationId = application.getOid();
        applicationId = applicationId.substring(applicationId.lastIndexOf('.') + 1);

        ctx.put("applicationSystemId", getFormName(application, applicationSystem));
        ctx.put("applicant", getApplicantName(application));
        ctx.put("applicationId", applicationId);
        ctx.put("applicationDate", receivedDate);
        ctx.put("modifiedDate", modifiedDate);
        ctx.put("preferences", getPreferences(application, applicationSystem));
        ctx.put("athlete", isAthlete(application));
        ctx.put("discretionary", isDiscretionary(application));
        ctx.put("musiikkiTanssiLiikuntaEducationCode", isMusiikkiTanssiLiikuntaEducationCode(application));
        ctx.put("attachmentRequests", attachmentRequests(application, locale));
        ctx.put("expires", dateFmt.format(new Date(getModificationLinkExpiration(applicationSystem))));
        ctx.put("lomakeTulostusLiite", resourceBundle.getString("lomake.tulostus.liite"));
        ctx.put("lomakeTulostusLiiteToimitusosoite", resourceBundle.getString("lomake.tulostus.liite.toimitusosoite"));
        ctx.put("lomakeTulostusLiiteDeadline", resourceBundle.getString("lomake.tulostus.liite.deadline"));
        ctx.put("nonSecurelinkEmail", doesNotUseSecurelink(applicationSystem));

        return ctx;
    }

    private List<Map<String, String>> attachmentRequests(final Application application, final Locale locale) {
        return Lists.transform(application.getAttachmentRequests(), new Function<ApplicationAttachmentRequest, Map<String, String>>() {
            @Override
            public Map<String, String> apply(ApplicationAttachmentRequest input) {
                ApplicationAttachment applicationAttachment = input.getApplicationAttachment();

                notNull(applicationAttachment.getAddress());

                return ImmutableMap.<String, String>builder()
                        .put("name", getTextOrEmpty(applicationAttachment.getName(), locale))
                        .put("header", getTextOrEmpty(applicationAttachment.getHeader(), locale))
                        .put("description", getTextOrEmpty(applicationAttachment.getDescription(), locale))
                        .put("recipient", defaultString(applicationAttachment.getAddress().getRecipient()))
                        .put("streetAddress", defaultString(applicationAttachment.getAddress().getStreetAddress()))
                        .put("streetAddress2", defaultString(applicationAttachment.getAddress().getStreetAddress2()))
                        .put("postalCode", defaultString(applicationAttachment.getAddress().getPostalCode()))
                        .put("postOffice", defaultString(applicationAttachment.getAddress().getPostOffice()))
                        .put("emailAddress", defaultString(applicationAttachment.getEmailAddress()))
                        .put("deadline", applicationAttachment.getDeadline() != null ?
                                dateTimeFormatter(locale).format(applicationAttachment.getDeadline()) : "")
                        .put("deliveryNote", getTextOrEmpty(applicationAttachment.getDeliveryNote(), locale))
                        .build();
            }
        });
    }

    private String getApplicantName(Application application) {
        String firstName = application.getVastauksetMerged().get(ELEMENT_ID_FIRST_NAMES);
        String lastName = application.getVastauksetMerged().get(ELEMENT_ID_LAST_NAME);
        return firstName + " " + lastName;
    }

    private String getFormName(Application application, ApplicationSystem applicationSystem) {
        String lang = application.getVastauksetMerged().get(ELEMENT_ID_CONTACT_LANGUAGE);
        String realLang = "fi";
        if (lang.equals("ruotsi")) {
            realLang = "sv";
        } else if (lang.equals("englanti")) {
            realLang = "en";
        }
        String formName = applicationSystem.getName().getText(realLang);
        if (isEmpty(formName)) {
            formName = applicationSystem.getName().getText("fi");
        }
        return formName;
    }

    private Object getPreferences(Application application, ApplicationSystem applicationSystem) {
        Map<String, String> answers = application.getVastauksetMerged();
        int maxPrefs = applicationSystem.getMaxApplicationOptions();
        List<String> preferences = new ArrayList<String>(maxPrefs);
        for (int i = 1; i <= maxPrefs; i++) {
            String koulutus = answers.get(String.format(PREFERENCE_NAME, i));
            String koulu = answers.get(String.format(PREFERENCE_ORGANIZATION, i));
            if (isEmpty(koulutus) && isEmpty(koulu)) {
                break;
            }
            koulutus = isEmpty(koulutus) ? "" : koulutus;
            koulu = isEmpty(koulu) ? "" : koulu;
            preferences.add(i + ". " + koulu + "\n   " + koulutus);
        }
        return preferences;
    }

    private boolean isMusiikkiTanssiLiikuntaEducationCode(Application application) {
        Map<String, String> answers = application.getVastauksetMerged();
        return (MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES.contains(answers.get(String.format(EDUCATION_CODE_KEY, 1))) ||
                MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES.contains(answers.get(String.format(EDUCATION_CODE_KEY, 2))) ||
                MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES.contains(answers.get(String.format(EDUCATION_CODE_KEY, 3))) ||
                MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES.contains(answers.get(String.format(EDUCATION_CODE_KEY, 4))) ||
                MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES.contains(answers.get(String.format(EDUCATION_CODE_KEY, 5))));
    }

    private boolean isAthlete(Application application) {
        Map<String, String> answers = application.getVastauksetMerged();
        return (TRUE.equals(answers.get("preference1_urheilijan_ammatillisen_koulutuksen_lisakysymys")) ||
                TRUE.equals(answers.get("preference1_urheilijalinjan_lisakysymys")) ||
                TRUE.equals(answers.get("preference2_urheilijan_ammatillisen_koulutuksen_lisakysymys")) ||
                TRUE.equals(answers.get("preference2_urheilijalinjan_lisakysymys")) ||
                TRUE.equals(answers.get("preference3_urheilijan_ammatillisen_koulutuksen_lisakysymys")) ||
                TRUE.equals(answers.get("preference3_urheilijalinjan_lisakysymys")) ||
                TRUE.equals(answers.get("preference4_urheilijan_ammatillisen_koulutuksen_lisakysymys")) ||
                TRUE.equals(answers.get("preference4_urheilijalinjan_lisakysymys")) ||
                TRUE.equals(answers.get("preference5_urheilijan_ammatillisen_koulutuksen_lisakysymys")) ||
                TRUE.equals(answers.get("preference5_urheilijalinjan_lisakysymys")));
    }

    private static boolean doesNotUseSecurelink(ApplicationSystem as) {
        return HAKUTAPA_JATKUVA_HAKU.equals(as.getHakutapa())
                || KOHDEJOUKKO_ERITYISOPETUKSENA_JARJESTETTAVA_AMMATILLINEN.equals(as.getKohdejoukkoUri())
                || KOHDEJOUKON_TARKENNE_SIIRTOHAKU.equals(as.getKohdejoukonTarkenne());
    }

    private boolean isDiscretionary(final Application application) {
        return !ApplicationUtil.getDiscretionaryAttachmentAOIds(application).isEmpty();
    }

    protected enum EducationDegree {
        SECONDARY, HIGHER
    }

    protected enum TemplateType {
        RECEIVED("email.application.received.title"),
        RECEIVED_HUOLTAJA("email.application.receivedhuoltaja.title"),
        MODIFIED("email.application.modified.title"),
        MODIFIED_HUOLTAJA("email.application.modifiedhuoltaja.title");

        public String subjectKey;

        TemplateType(String subjectKey) {
            this.subjectKey = subjectKey;
        }
    }

    private static class TemplateKey {
        final Locale locale;
        final EducationDegree educationDegree;
        final TemplateType templateType;

        public TemplateKey(Locale locale, EducationDegree educationDegree, TemplateType templateType) {
            notNull(locale);
            notNull(educationDegree);
            notNull(templateType);

            this.locale = locale;
            this.educationDegree = educationDegree;
            this.templateType = templateType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TemplateKey that = (TemplateKey) o;

            if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;
            if (educationDegree != that.educationDegree) return false;
            return templateType == that.templateType;

        }

        @Override
        public int hashCode() {
            int result = locale != null ? locale.hashCode() : 0;
            result = 31 * result + (educationDegree != null ? educationDegree.hashCode() : 0);
            result = 31 * result + (templateType != null ? templateType.hashCode() : 0);
            return result;
        }
    }

}
