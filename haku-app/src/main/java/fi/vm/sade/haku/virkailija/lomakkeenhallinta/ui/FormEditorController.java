package fi.vm.sade.haku.virkailija.lomakkeenhallinta.ui;

import fi.vm.sade.haku.oppija.hakemus.resource.JSONException;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGenerator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Path("/application-system-form-editor")
public class FormEditorController {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";


    private static final Logger LOGGER = LoggerFactory.getLogger(FormEditorController.class);

    private final HakuService hakuService;
    private final FormGenerator formaGenerator;

    @Autowired
    public FormEditorController(HakuService hakuService, FormGenerator formaGenerator) {
        this.hakuService = hakuService;
        this.formaGenerator = formaGenerator;
    }

    @GET
    @Path("application-system-form")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public List<Map<String, Object>> getApplicationSystemForms(){
        ArrayList<Map<String,Object>> applicationSystemForms = new ArrayList<Map<String, Object>>();
        for (ApplicationSystem applicationSystem : hakuService.getApplicationSystems()){
            Map<String, Object> applicationSystemForm = new HashMap<String, Object>();
            applicationSystemForm.put("_id", applicationSystem.getId());
            applicationSystemForm.put("name", applicationSystem.getName());
            applicationSystemForm.put("kausi", applicationSystem.getHakukausiUri());
            applicationSystemForm.put("vuosi", applicationSystem.getHakukausiVuosi());
            applicationSystemForm.put("tyyppi", applicationSystem.getApplicationSystemType());
            applicationSystemForm.put("pohja", applicationSystem.getApplicationSystemType());
            //applicationSystemForm.put("state",null);
            applicationSystemForms.add(applicationSystemForm);
        }
        return applicationSystemForms;
    }

    @GET
    @Path("application-system-form/{applicationSystemId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public Map getAppicationSystemForm(@PathParam("applicationSystemId") String applicationSystemId){
        ApplicationSystem applicationSystem = formaGenerator.generate(applicationSystemId);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);

        return mapper.convertValue(applicationSystem.getForm(), Map.class);
    }

    @GET
    @Path("application-system-themes/{applicationSystemId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public Map getApplicationSystemThemes(@PathParam("applicationSystemId") String applicationSystemId){
        throw new JSONException(Response.Status.NOT_FOUND, "Not implemented yet", null);
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
