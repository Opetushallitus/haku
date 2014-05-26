/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.resources;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionQueryParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.SimpleObjectIdSerializer;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.*;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGenerator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@Path("/lomakkeenhallinta/themequestion")
public class ThemeQuestionResource {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeQuestionResource.class);

    private final ThemeQuestionDAO themeQuestionDAO;
    private final HakuService hakuService;
    private final FormGenerator formaGenerator;

    @Autowired
    public ThemeQuestionResource(ThemeQuestionDAO themeQuestionDAO, HakuService hakuService, FormGenerator formaGenerator) {
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakuService = hakuService;
        this.formaGenerator = formaGenerator;
    }

    @GET
    @Path("question/{themeQuestionId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public ThemeQuestion getThemedQuestionByOid(@PathParam("themeQuestionId") String themeQuestionId) {
        LOGGER.debug("Getting question by: {}", themeQuestionId);
        return themeQuestionDAO.findById(themeQuestionId);
    }

    @GET
    @Path("{applicationSystemId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public List<ThemeQuestion> getThemeQuestionsByApplicationSystem(@PathParam("applicationSystemId") String applicationSystemId){
        ThemeQuestionQueryParameters tqq = new ThemeQuestionQueryParameters();
        tqq.setApplicationSystemId(applicationSystemId);
        List<ThemeQuestion> themeQuestions = themeQuestionDAO.query(tqq);
        return themeQuestions;
    }


    @GET
    @Path("{applicationSystemId}/{learningOpportunityId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @JsonSerialize(using= SimpleObjectIdSerializer.class,
      as=ObjectId.class)
    public List<ThemeQuestion> getThemeQuestion(@PathParam("applicationSystemId") String applicationSystemId, @PathParam("learningOpportunityId") String learningOpportunityId) {
        ThemeQuestionQueryParameters tqq = new ThemeQuestionQueryParameters();
        tqq.setApplicationSystemId(applicationSystemId);
        tqq.setLearningOpportunityId(learningOpportunityId);
        List<ThemeQuestion> themeQuestions = themeQuestionDAO.query(tqq);
        return themeQuestions;
    }

    @GET
    @Path("{applicationSystemId}/{learningOpportunityId}/{themeQuestionId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public ThemeQuestion getThemedQuestionByPath(@PathParam("themeQuestionId") String themeQuestionId) {
        LOGGER.debug("Getting question by: {}", themeQuestionId);
        return themeQuestionDAO.findById(themeQuestionId);
    }

    @POST
    @Path("{applicationSystemId}/{learningOpportunityId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public void postThemeQuestion(@PathParam("applicationSystemId") String applicationSystemId,
                                    @PathParam("learningOpportunityId") String learningOpportunityId,
                                    ThemeQuestion themeQuestion) {
        LOGGER.debug("Got " + themeQuestion);
        themeQuestionDAO.save(themeQuestion);
    }

    //TODO: @FIX Move later to a sane location and fix. Return supported types with translations
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

    //TODO: @FIX Move later to a sane location. Returns translations for languages
    @GET
    @Path("languages")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
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

    //TODO: @FIX Move later to a sane location. Returns all configure applicationSystems
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

    //TODO: @FIX Move later to a sane location. Returns a configured form
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


}
