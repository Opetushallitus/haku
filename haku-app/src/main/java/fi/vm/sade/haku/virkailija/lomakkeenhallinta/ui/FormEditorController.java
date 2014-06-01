package fi.vm.sade.haku.virkailija.lomakkeenhallinta.ui;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.resource.JSONException;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGenerator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Controller
@Path("/application-system-form-editor")
public class FormEditorController {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    public enum State {
        ACTIVE, LOCKED, PUBLISHED, CLOSED, ERROR
    }

    private static final Map<State, I18nText> stateTranslations = new ImmutableMap.Builder<State,I18nText>().put(State.ACTIVE, new I18nText(ImmutableMap.of("fi", "Aktiivinen"))).
      put(State.LOCKED, new I18nText(ImmutableMap.of("fi", "Lukittu"))).put(State.PUBLISHED, new I18nText(ImmutableMap.of("fi", "Julkaistu"))).
      put(State.CLOSED, new I18nText(ImmutableMap.of("fi", "Suljettu"))).put(State.ERROR, new I18nText(ImmutableMap.of("fi", "Virheellinen"))).build();

    private static final Logger LOGGER = LoggerFactory.getLogger(FormEditorController.class);

    @Autowired
    private HakuService hakuService;

    @Autowired
    private FormGenerator formaGenerator;

    private static final String[] UNEDITABLE_THEME_FILTERS = {"henkilotiedot", "koulutustausta"};

    public FormEditorController() {
    }

    @Autowired
    public FormEditorController(HakuService hakuService, FormGenerator formaGenerator) {
        this.hakuService = hakuService;
        this.formaGenerator = formaGenerator;
    }

    @GET
    @Path("application-system-form")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD')")
    public List<Map<String, Object>> getApplicationSystemForms(){
        ArrayList<Map<String,Object>> applicationSystemForms = new ArrayList<Map<String, Object>>();
        for (ApplicationSystem applicationSystem : hakuService.getApplicationSystems()){
            Map<String, Object> applicationSystemForm = new HashMap<String, Object>();
            applicationSystemForm.put("_id", applicationSystem.getId());
            applicationSystemForm.put("name", applicationSystem.getName());
            applicationSystemForm.put("period", applicationSystem.getHakukausiUri());
            applicationSystemForm.put("year", applicationSystem.getHakukausiVuosi());
            applicationSystemForm.put("type", applicationSystem.getApplicationSystemType());
            applicationSystemForm.put("template", applicationSystem.getApplicationSystemType());
            applicationSystemForm.put("status", stateTranslations.get(deduceApplicationSystemState(applicationSystem)));
            applicationSystemForms.add(applicationSystemForm);
        }
        return applicationSystemForms;
    }

    private State deduceApplicationSystemState(ApplicationSystem applicationSystem){
        List<ApplicationPeriod> applicationPeriods = applicationSystem.getApplicationPeriods();
        if (applicationPeriods.size() != 1 ){
            LOGGER.error("Unexcepted number of periods. Got {} for application system {}", applicationPeriods.size(), applicationSystem.getId());
            if (applicationPeriods.size() < 1)
                return State.ERROR;
        }
        ApplicationPeriod applicationPeriod = applicationPeriods.get(0);
        final Date now = new Date();
        LOGGER.debug("Decucing state. Now {}. Period Start {}. Period End {}", now, applicationPeriod.getStart(),applicationPeriod.getEnd());
        if (now.after(applicationPeriod.getEnd())){
            return State.CLOSED;
        }
        if ( now.after(applicationPeriod.getStart())) {
            return State.PUBLISHED;
        }
        // TODO: FIX Hardcoding locked to 2 days before application period starts
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(applicationPeriod.getStart());
        calendar.roll(Calendar.DATE,-2);
        LOGGER.debug("Testing against {} ", calendar.getTime());
        if (now.after(calendar.getTime())){
            return State.LOCKED;
        }
        return State.ACTIVE;
    }

    @GET
    @Path("application-system-form/{applicationSystemId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD')")
    public Map getAppicationSystemForm(@PathParam("applicationSystemId") String applicationSystemId){
        ApplicationSystem applicationSystem = formaGenerator.generate(applicationSystemId);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);

        return mapper.convertValue(applicationSystem.getForm(), Map.class);
    }

    @GET
    @Path("application-system-form/{applicationSystemId}/additional-question-themes")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKEMUS_READ_UPDATE', 'ROLE_APP_HAKEMUS_READ', 'ROLE_APP_HAKEMUS_CRUD')")
    public List<Map<String, Object>> getAdditinalQuestionThemes(@PathParam("applicationSystemId") String applicationSystemId){
        LOGGER.debug("Generating application system with id: "+ applicationSystemId);
        ApplicationSystem applicationSystem = formaGenerator.generate(applicationSystemId);
        List<Element> phaseElements = applicationSystem.getForm().getChildren();
        List<Map<String, Object>> themes = new ArrayList<Map<String, Object>>();
        for(Element phase : phaseElements){
           if (! (phase instanceof Phase)){
                LOGGER.debug("First level child not a phase element in form for applicationSystem. Got " + phase.getType() + " instead." );
                continue;
            }
            themes.addAll(parsePhaseThemes((Phase) phase));
        }
        return themes;
    }

    private final List<Map<String, Object>> parsePhaseThemes(Phase phase) {
        List<Element> themeElements = phase.getChildren();
        List<Map<String, Object>> themes = new ArrayList<Map<String, Object>>();
        for (Element themeElement : themeElements) {
            if (!(themeElement instanceof Theme)) {
                LOGGER.warn("First level child of phase " + phase.getId() + " not a theme element. Got " + themeElement.getType() + " instead.");
                continue;
            }
            Boolean filtered = Boolean.FALSE;
            for (String filter : UNEDITABLE_THEME_FILTERS) {
                if (themeElement.getId().toLowerCase().contains(filter))
                    filtered = Boolean.TRUE;
            }
            if (filtered)
                LOGGER.debug("Filtered theme " + themeElement.getId());
            else{
                Map<String, Object> themeMap = new HashMap<String, Object>(2);
                themeMap.put("id", themeElement.getId());
                themeMap.put("name", ((Theme) themeElement).getI18nText());
                themes.add(themeMap);
            }
        }
        return themes;
    }


    /*
    Support Methods
     */

    //
    //Return supported types with translations
    @GET
    @Path("types")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public List getSupportedTypes(){
        ArrayList supportedTypes = new ArrayList();

        Map<String, String> textQuestion = new HashMap<String,String>();
        textQuestion.put("fi", "Avoin vastaus (tekstikenttä)");
        textQuestion.put( "sv", "Avoin vastaus (tekstikenttä) (sv)");
        textQuestion.put("en", "Avoin vastaus (textfield) (en)");

        Map supportedType = new HashMap();
        supportedType.put("id", "TextQuestion");
        supportedType.put("name", new I18nText(textQuestion));
        supportedTypes.add(supportedType);

        Map<String, String> checkBox = new HashMap<String,String>();
        checkBox.put("fi", "Valinta kysymys (valintalaatikko)");
        checkBox.put("sv", "Valinta kysymys (valintalaatikko) (sv)");
        checkBox.put("en", "Valinta kysymys (checkbox) (en)");

        supportedType = new HashMap();
        supportedType.put("id", "CheckBox");
        supportedType.put("name", new I18nText(checkBox));
        supportedTypes.add(supportedType);

        Map<String, String> radioButton = new HashMap<String,String>();
        radioButton.put("fi", "Valinta kysymys (valintanappi)");
        radioButton.put("sv", "Valinta kysymys (valintanappi) (sv)");
        radioButton.put("en", "Valinta kysymys (radiobutton) (en)");

        supportedType = new HashMap();
        supportedType.put("id", "RadioButton");
        supportedType.put("name", new I18nText(radioButton));
        supportedTypes.add(supportedType);
        return supportedTypes;
    }

    //Returns translations for languages
    @GET
    @Path("languages")
    @Produces(value = MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public Map<String, I18nText> getLanguages(){
        Map<String, String> fi_tranlations = new HashMap<String,String>();
        fi_tranlations.put("fi", "Suomi");
        fi_tranlations.put( "sv", "Suomi (sv)");
        fi_tranlations.put("en", "Suomi (en)");

        Map<String, String> sv_tranlations = new HashMap<String,String>();
        sv_tranlations.put("fi", "Ruotsi");
        sv_tranlations.put("sv", "Ruotsi (sv)");
        sv_tranlations.put("en", "Ruotsi (en)");

        Map<String, String> en_tranlations = new HashMap<String,String>();
        en_tranlations.put("fi", "Englanti");
        en_tranlations.put("sv", "Englanti (sv)");
        en_tranlations.put("en", "Englanti (en)");
        Map<String, I18nText> languages = new HashMap<String, I18nText>();
        languages.put("fi", new I18nText(fi_tranlations));
        languages.put("sv", new I18nText(sv_tranlations));
        languages.put("en", new I18nText(en_tranlations));
        return languages;
    }
}
