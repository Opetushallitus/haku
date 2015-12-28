package fi.vm.sade.haku.oppija.postprocess.impl;

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
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.mail.EmailException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.phase.valmis.ValmisPhase.MUSIIKKI_TANSSI_LIIKUNTA_EDUCATION_CODES;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.EDUCATION_CODE_KEY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class SendMailService {

    private final ApplicationSystemService applicationSystemService;
    private final FormService formService;

    final private Map<String, Template> templateMap = new HashMap<String, Template>();
    final private Map<String, Template> templateMapHigherEducation =new HashMap<String, Template>();

    public static final String TRUE = "true";

    @Value("${mode.demo:false}")
    public boolean demoMode;

    @Value("${email.application.modify.link.fi}")
    String emailApplicationModifyLinkFi;
    @Value("${email.application.modify.link.sv}")
    String emailApplicationModifyLinkSv;
    @Value("${email.application.modify.link.en}")
    String emailApplicationModifyLinkEn;

    @Value("${oppijantunnistus.create.url}")
    String oppijanTunnistusUrl;

    final RestClient restClient;

    @Autowired
    public SendMailService(final ApplicationSystemService applicationSystemService,
                           final FormService formService, final RestClient restClient){
        this.applicationSystemService = applicationSystemService;
        this.formService = formService;
        this.restClient = restClient;
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
        templateMap.put("suomi", velocityEngine.getTemplate("email/application_received_fi.vm", "UTF-8"));
        templateMap.put("ruotsi", velocityEngine.getTemplate("email/application_received_sv.vm", "UTF-8"));
        templateMap.put("englanti", velocityEngine.getTemplate("email/application_received_en.vm", "UTF-8"));
        templateMapHigherEducation.put("suomi", velocityEngine.getTemplate("email/application_received_higher_ed_fi.vm", "UTF-8"));
        templateMapHigherEducation.put("ruotsi", velocityEngine.getTemplate("email/application_received_higher_ed_sv.vm", "UTF-8"));
        templateMapHigherEducation.put("englanti", velocityEngine.getTemplate("email/application_received_higher_ed_en.vm", "UTF-8"));
    }


    public void sendMail(Application application) throws EmailException {
        if(!demoMode) {
            Map<String, String> answers = application.getVastauksetMerged();
            String email = answers.get(OppijaConstants.ELEMENT_ID_EMAIL);
            if (!isEmpty(email)) {
                sendConfirmationMail(application);
            }
        }
    }

    private void sendConfirmationMail(final Application application) throws EmailException {
        Map<String, String> answers = application.getVastauksetMerged();
        final String emailAddress = answers.get(OppijaConstants.ELEMENT_ID_EMAIL);
        String lang = answers.get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE);

        Template tmpl = templateMap.get(lang);
        String asOid = application.getApplicationSystemId();
        final ApplicationSystem as = applicationSystemService.getApplicationSystem(asOid);
        if (as.getKohdejoukkoUri().equals(OppijaConstants.KOHDEJOUKKO_KORKEAKOULU)) {
            tmpl = templateMapHigherEducation.get(lang);
        }

        Locale locale = new Locale("fi");
        if ("ruotsi".equals(lang)) {
            locale = new Locale("sv");
        } else if ("englanti".equals(lang)) {
            locale = new Locale("en");
        }
        ResourceBundle messages = ResourceBundle.getBundle("messages", locale);

        final String emailSubject = messages.getString("email.application.received.title");
        StringWriter sw = new StringWriter();
        VelocityContext ctx = buildContext(application, as, locale, messages);
        tmpl.merge(ctx, sw);
        final String emailTemplate = sw.toString();

        final Map<LanguageCodeISO6391, String> langToLink = ImmutableMap.of(
                LanguageCodeISO6391.fi, emailApplicationModifyLinkFi,
                LanguageCodeISO6391.sv, emailApplicationModifyLinkSv,
                LanguageCodeISO6391.en, emailApplicationModifyLinkEn
        );

        final LanguageCodeISO6391 emailLang = LanguageCodeISO6391.valueOf(locale.toString());
        OppijanTunnistusDTO body = new OppijanTunnistusDTO(){{
            this.url = langToLink.get(emailLang);
            this.expires = getModificationLinkExpiration(application, as);
            this.email = emailAddress;
            this.subject = emailSubject;
            this.template = emailTemplate;
            this.lang = emailLang;
            this.metadata = new Metadata() {{
                this.hakemusOid = application.getOid();
            }};
        }};

        try {
            boolean successStatusCode = Futures.transform(restClient.post(oppijanTunnistusUrl, body, Object.class), new Function<HttpRestClient.Response<Object>, Boolean>() {
                @Override
                public Boolean apply(HttpRestClient.Response<Object> input) {
                    return input.isSuccessStatusCode();
                }
            }).get();
            if (!successStatusCode) {
                throw new EmailException("OppijanTunnistus status code did not indicate success");
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new EmailException("OppijanTunnistus request failed: " + e);
        }
    }

    private long getModificationLinkExpiration(Application application, ApplicationSystem as) {
        return application.getApplicationPeriodWhenSubmitted(as.getApplicationPeriods()).getEnd().getTime();
    }

    private VelocityContext buildContext(Application application, ApplicationSystem applicationSystem, Locale locale, ResourceBundle resourceBundle) {
        VelocityContext ctx = new VelocityContext();
        DateFormat dateFmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String applicationDate = dateFmt.format(application.getReceived());
        String applicationId = application.getOid();
        applicationId = applicationId.substring(applicationId.lastIndexOf('.') + 1);

        ctx.put("applicationSystemId", getFormName(application));
        ctx.put("applicant", getApplicantName(application));
        ctx.put("applicationId", applicationId);
        ctx.put("applicationDate", applicationDate);
        ctx.put("preferences", getPreferences(application, applicationSystem));
        ctx.put("athlete", isAthlete(application));
        ctx.put("discretionary", isDiscretionary(application));
        ctx.put("musiikkiTanssiLiikuntaEducationCode", isMusiikkiTanssiLiikuntaEducationCode(application));
        ctx.put("attachmentRequests", attachmentRequests(application, locale));
        ctx.put("lomakeTulostusLiite", resourceBundle.getString("lomake.tulostus.liite"));
        ctx.put("lomakeTulostusLiiteToimitusosoite", resourceBundle.getString("lomake.tulostus.liite.toimitusosoite"));
        ctx.put("lomakeTulostusLiiteDeadline", resourceBundle.getString("lomake.tulostus.liite.deadline"));

        return ctx;
    }

    private List<Map<String, String>> attachmentRequests(Application application, final Locale locale) {
        return Lists.transform(application.getAttachmentRequests(), new Function<ApplicationAttachmentRequest, Map<String, String>>() {
            @Override
            public Map<String, String> apply(ApplicationAttachmentRequest input) {
                ApplicationAttachment applicationAttachment = input.getApplicationAttachment();
                DateFormat f = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
                return ImmutableMap.<String, String>builder()
                        .put("name", applicationAttachment.getName().getText(locale.getLanguage().toLowerCase()))
                        .put("header", applicationAttachment.getHeader().getText(locale.getLanguage().toLowerCase()))
                        .put("description", applicationAttachment.getDescription().getText(locale.getLanguage().toLowerCase()))
                        .put("recipient", applicationAttachment.getAddress().getRecipient())
                        .put("streetAddress", applicationAttachment.getAddress().getStreetAddress())
                        .put("streetAddress2", applicationAttachment.getAddress().getStreetAddress2())
                        .put("postalCode", applicationAttachment.getAddress().getPostalCode())
                        .put("postOffice", applicationAttachment.getAddress().getPostOffice())
                        .put("emailAddress", applicationAttachment.getEmailAddress())
                        .put("deadline", f.format(applicationAttachment.getDeadline()))
                        .put("deliveryNote", applicationAttachment.getDeliveryNote().getText(locale.getLanguage().toLowerCase()))
                        .build();
            }
        });
    }

    private String getApplicantName(Application application) {
        String firstName = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_FIRST_NAMES);
        String lastName = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_LAST_NAME);
        return firstName + " " + lastName;
    }

    private String getFormName(Application application) {
        Form form = formService.getForm(application.getApplicationSystemId());
        Map<String, String> translations = form.getI18nText().getTranslations();
        String lang = application.getVastauksetMerged().get(OppijaConstants.ELEMENT_ID_CONTACT_LANGUAGE);
        String realLang = "fi";
        if (lang.equals("ruotsi")) {
            realLang = "sv";
        } else if (lang.equals("englanti")) {
            realLang = "en";
        }
        String formName = translations.get(realLang);
        if (isEmpty(formName)) {
            formName = translations.get("fi");
        }
        return formName;
    }

    private Object getPreferences(Application application, ApplicationSystem applicationSystem) {
        Map<String, String> answers = application.getVastauksetMerged();
        int maxPrefs = applicationSystem.getMaxApplicationOptions();
        List<String> preferences = new ArrayList<String>(maxPrefs);
        for (int i = 1; i <= maxPrefs; i++) {
            String koulutus = answers.get(String.format(OppijaConstants.PREFERENCE_NAME, i));
            String koulu = answers.get(String.format(OppijaConstants.PREFERENCE_ORGANIZATION, i));
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

    private boolean isDiscretionary(final Application application) {
        return !ApplicationUtil.getDiscretionaryAttachmentAOIds(application).isEmpty();
    }

}
