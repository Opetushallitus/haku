package fi.vm.sade.haku.virkailija.lomakkeenhallinta.ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationHierarchy;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.resource.JSONException;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.haku.virkailija.authentication.KayttooikeusService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGenerator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Map<ApplicationSystem.State, I18nText> stateTranslations =
            new ImmutableMap.Builder<ApplicationSystem.State,I18nText>()
                    .put(ApplicationSystem.State.ACTIVE, new I18nText(ImmutableMap.of("fi", "Aktiivinen")))
                    .put(ApplicationSystem.State.LOCKED, new I18nText(ImmutableMap.of("fi", "Lukittu")))
                    .put(ApplicationSystem.State.PUBLISHED, new I18nText(ImmutableMap.of("fi", "Julkaistu")))
                    .put(ApplicationSystem.State.CLOSED, new I18nText(ImmutableMap.of("fi", "Suljettu")))
                    .put(ApplicationSystem.State.ERROR, new I18nText(ImmutableMap.of("fi", "Virheellinen")))
                    .build();

    private static final Map<String, Object> hakutoiveTheme =
            new ImmutableMap.Builder<String,Object>()
                    .put("id", "hakutoiveet_teema")
                    .put("name", new I18nText(ImmutableMap.of("fi", "Hakutoiveet", "sv", "Ansökningsönskemål")))
                    .build();

    private static final Map<String, Object> arvosanaTheme =
            new ImmutableMap.Builder<String,Object>().put("id", "arvosanat")
                    .put("name", new I18nText(ImmutableMap.of("fi", "Arvosanat", "sv", "Vitsord")))
                    .build();

    private static final Map<String, Object> lupaTiedotTheme =
            new ImmutableMap.Builder<String,Object>()
                    .put("id", "lupatiedot")
                    .put("name", new I18nText(ImmutableMap.of("fi", "Lupatiedot", "sv", "Tillståndsuppgifter")))
                    .build();

    private static final Map<String, I18nText> questionTypeTranslations =
            new ImmutableMap.Builder<String, I18nText>()
                    .put("TextQuestion", new I18nText(new ImmutableMap.Builder<String, String>()
                            .put("fi", "Avoin vastaus (tekstikenttä)")
                            .put("sv", "Avoin vastaus (tekstikenttä) (sv)")
                            .put("en", "Avoin vastaus (textfield) (en)")
                            .build()))
                    .put("RichText", new I18nText(new ImmutableMap.Builder<String, String>()
                            .put("fi", "Infoteksti")
                            .put("sv", "Infotext")
                            .put("en", "Infoteksti (en)")
                            .build()))
                    .put("CheckBox", new I18nText(new ImmutableMap.Builder<String, String>()
                            .put("fi", "Valinta kysymys (valintaruutu)")
                            .put("sv", "Valinta kysymys (valintaruutu) (sv)")
                            .put("en", "Valinta kysymys (checkbox) (en)")
                            .build()))
                    .put("RadioButton", new I18nText(new ImmutableMap.Builder<String, String>()
                            .put("fi", "Valinta kysymys (valintanappi)")
                            .put("sv", "Valinta kysymys (valintanappi) (sv)")
                            .put("en", "Valinta kysymys (radiobutton) (en)")
                            .build()))
                    .build();

    private static final Logger LOGGER = LoggerFactory.getLogger(FormEditorController.class);

    @Autowired
    private HakuService hakuService;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private FormGenerator formGenerator;

    @Autowired
    private KayttooikeusService kayttooikeusService;

    @Autowired
    private OrganizationService organizationService;

    public FormEditorController() {
    }

    @Autowired
    public FormEditorController(HakuService hakuService, FormGenerator formGenerator,
                                KayttooikeusService kayttooikeusService, HakukohdeService hakukohdeService,
                                OrganizationService organizationService) {
        this.hakuService = hakuService;
        this.formGenerator = formGenerator;
        this.kayttooikeusService = kayttooikeusService;
        this.hakukohdeService = hakukohdeService;
        this.organizationService = organizationService;
    }

    @GET
    @Path("application-system-form")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD', 'ROLE_APP_HAKULOMAKKEENHALLINTA_READ')")
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
            applicationSystemForm.put("status", stateTranslations.get(applicationSystem.getApplicationSystemState()));
            applicationSystemForms.add(applicationSystemForm);
        }
        return applicationSystemForms;
    }

    @GET
    @Path("application-system-form/{applicationSystemId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD', " +
            "'ROLE_APP_HAKULOMAKKEENHALLINTA_READ')")
    public Map getAppicationSystemForm(@PathParam("applicationSystemId") String applicationSystemId){
        ApplicationSystem applicationSystem = formGenerator.generate(applicationSystemId);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);

        return mapper.convertValue(applicationSystem.getForm(), Map.class);
    }

    @GET
    @Path("application-system-form/{applicationSystemId}/full")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD', " +
            "'ROLE_APP_HAKULOMAKKEENHALLINTA_READ')")
    public Map getAppicationSystem(@PathParam("applicationSystemId") String applicationSystemId){
        ApplicationSystem applicationSystem = formGenerator.generate(applicationSystemId);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);

        return mapper.convertValue(applicationSystem, Map.class);
    }

    @GET
    @Path("application-system-form/{applicationSystemId}/state")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD', " +
            "'ROLE_APP_HAKULOMAKKEENHALLINTA_READ')")
    public Map getAppicationSystemState(@PathParam("applicationSystemId") String applicationSystemId){
        return ImmutableMap.of("State", hakuService.getApplicationSystem(applicationSystemId).getApplicationSystemState());
    }

    @GET
    @Path("application-system-form/{applicationSystemId}/name")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD', " +
            "'ROLE_APP_HAKULOMAKKEENHALLINTA_READ')")
    public Map<String, I18nText> getApplicationSystemForms(@PathParam("applicationSystemId") String applicationSystemId){
        ApplicationSystem applicationSystem = hakuService.getApplicationSystem(applicationSystemId);
        if (applicationSystem == null)
            throw new JSONException(Response.Status.NOT_FOUND, "ApplicationSystem not found with id "+ applicationSystemId, null);
        return ImmutableMap.of("name", applicationSystem.getName());
    }

    @GET
    @Path("application-system-form/{applicationSystemId}/hakuajatJaHakutapa")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD', " +
            "'ROLE_APP_HAKULOMAKKEENHALLINTA_READ')")
    public Map<String, Object> getApplicationPeriods(@PathParam("applicationSystemId") String applicationSystemId){
        ApplicationSystem applicationSystem = hakuService.getApplicationSystem(applicationSystemId);
        if (applicationSystem == null)
            throw new JSONException(Response.Status.NOT_FOUND, "ApplicationSystem not found with id "+ applicationSystemId, null);
        return ImmutableMap.of("hakutapa", applicationSystem.getHakutapa(), "hakuajat", applicationSystem.getApplicationPeriods());
    }

    @GET
    @Path("application-system-form/{applicationSystemId}/represented-organizations")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD', " +
            "'ROLE_APP_HAKULOMAKKEENHALLINTA_READ')")
    public List<Organization> getParticipatingUserOrganizations(@PathParam("applicationSystemId") String applicationSystemId) {
        List<String> applicationOptionIds = hakuService.getRelatedApplicationOptionIds(applicationSystemId);
        LOGGER.debug("Got " + (null == applicationOptionIds ? null
                : applicationOptionIds.size()) + " options for application system "+ applicationSystemId);
        if (null == applicationOptionIds)
            return new ArrayList<Organization>();

        //TODO cache this
        LOGGER.debug("Building hiararchy for " + applicationSystemId);
        OrganizationHierarchy orgHierarchy = new OrganizationHierarchy(organizationService);
        for (String applicationOptionId : applicationOptionIds) {
            LOGGER.debug("Fetching option data for " + applicationOptionId);
            HakukohdeV1RDTO applicationOption = hakukohdeService.findByOid(applicationOptionId);
            Iterator<String> providerIds = applicationOption.getTarjoajaOids().iterator();
            if (!providerIds.hasNext()){
                LOGGER.error("Got null provider for application option: " + applicationOptionId + " of application system: "
                        + applicationSystemId);
                continue;
            }
            // TODO jossain vaiheessa täytyy hoitaa useampi provider
            String providerId = providerIds.next();
            LOGGER.debug("Provider for  " + applicationOptionId + " is " +providerId);
            orgHierarchy.addOrganization(providerId);
        }
        List<String> henkOrgs = kayttooikeusService.getOrganisaatioHenkilo();
        LOGGER.debug("Got " + henkOrgs.size() + " organization for the user");
        HashSet<Organization> organizations = new HashSet<Organization>();
        for (String henkOrg : henkOrgs){
            organizations.addAll(orgHierarchy.getAllSubOrganizations(henkOrg));
        }
        return new ArrayList<Organization>(organizations);
    }

    @GET
    @Path("application-system-form/{applicationSystemId}/additional-question-themes")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public List<Map<String, Object>> getAdditinalQuestionThemes(@PathParam("applicationSystemId") String applicationSystemId){
        LOGGER.debug("Generating application system with id: "+ applicationSystemId);
        return getThemesFromApplicationSystem(applicationSystemId);

    }

    private final List<Map<String, Object>> generateThemes(){
        List<Map<String, Object>> themes = new ArrayList<Map<String, Object>>();
        themes.add(hakutoiveTheme);
        themes.add(arvosanaTheme);
        themes.add(lupaTiedotTheme);
        return themes;
    }

    private final List<Map<String, Object>> getThemesFromApplicationSystem(String applicationSystemId){
        Form applicationSystemForm = formGenerator.generateFormWithThemesOnly(applicationSystemId);
        List<Element> phaseElements = applicationSystemForm.getChildren();
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
                LOGGER.warn("First level child of phase " + phase.getId() + " not a theme element. Got " +
                        themeElement.getType() + " instead.");
                continue;
            }
            if (((Theme) themeElement).isConfigurable()){
                Map<String, Object> themeMap = new HashMap<String, Object>(2);
                themeMap.put("id", themeElement.getId());
                themeMap.put("name", ((Theme) themeElement).getI18nText());
                themes.add(themeMap);
            }
            else {
                LOGGER.debug("Theme " + themeElement.getId() + " not configurable");
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
        for (String key: questionTypeTranslations.keySet()){
            supportedTypes.add(ImmutableMap.of("id", key, "name", questionTypeTranslations.get(key)));
        }
        return supportedTypes;
    }

    @GET
    @Path("types/{type}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public List getSupportedType(@PathParam("type") String type){
        I18nText typeTranslation = questionTypeTranslations.get(type);
        return ImmutableList.of(ImmutableMap.of("id", type, "name", typeTranslation));
    }

    //Returns translations for languages
    @GET
    @Path("languages")
    @Produces(value = MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public List<Map<String, Object>> getLanguages(){
        List<Map<String, Object>> languages = new ArrayList<Map<String, Object>>();
        Map<String, String> fi_tranlations = new HashMap<String,String>();
        fi_tranlations.put("fi", "Suomi");
        fi_tranlations.put( "sv", "Suomi (sv)");
        fi_tranlations.put("en", "Suomi (en)");
        languages.add(new ImmutableMap.Builder().put("id", "fi").put("order", 1).put("translations", fi_tranlations).build());

        Map<String, String> sv_tranlations = new HashMap<String,String>();
        sv_tranlations.put("fi", "Ruotsi");
        sv_tranlations.put("sv", "Ruotsi (sv)");
        sv_tranlations.put("en", "Ruotsi (en)");
        languages.add(new ImmutableMap.Builder().put("id", "sv").put("order", 2).put("translations", sv_tranlations).build());

        Map<String, String> en_tranlations = new HashMap<String,String>();
        en_tranlations.put("fi", "Englanti");
        en_tranlations.put("sv", "Englanti (sv)");
        en_tranlations.put("en", "Englanti (en)");
        languages.add(new ImmutableMap.Builder().put("id", "en").put("order", 3).put("translations", en_tranlations).build());
        return languages;
    }
}
