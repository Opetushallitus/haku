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

import fi.vm.sade.haku.oppija.hakemus.resource.JSONException;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@Path("/application-system-form-editor/theme-question")
public class ThemeQuestionResource {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeQuestionResource.class);

    private final ThemeQuestionDAO themeQuestionDAO;


    @Autowired
    public ThemeQuestionResource(ThemeQuestionDAO themeQuestionDAO) {
        this.themeQuestionDAO = themeQuestionDAO;
    }

    @GET
    @Path("{themeQuestionId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public ThemeQuestion getThemeQuestionByOid(@PathParam("themeQuestionId") String themeQuestionId) {
        LOGGER.debug("Getting question by Id: {}", themeQuestionId);
        return themeQuestionDAO.findById(themeQuestionId);
    }

    @DELETE
    @Path("{themeQuestionId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public ThemeQuestion deleteThemeQuestionByOid(@PathParam("themeQuestionId") String themeQuestionId) {
        LOGGER.debug("Deleting theme question with id: {}", themeQuestionId);
        throw new JSONException(Response.Status.NOT_FOUND, "Not implemented yet", null);
    }

    @POST
    @Path("{themeQuestionId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public void updateThemeQuestion(@PathParam("themeQuestionId") String themeQuestionId,
                                    ThemeQuestion themeQuestion) {
        LOGGER.debug("Updating theme question with id: {}", themeQuestionId);

        if (!themeQuestionId.equals(themeQuestion.getId().toString())){
            throw new JSONException(Response.Status.BAD_REQUEST, "theme question id mismatch", null);
        }
        ThemeQuestion dbThemeQuestion = fetchThemeQuestion(themeQuestionId);

        LOGGER.debug("Saving Theme Question with id: " + dbThemeQuestion.getId().toString());
        themeQuestionDAO.save(themeQuestion);
        LOGGER.debug("Saved Theme Question with id: " + themeQuestionId);
    }

    private ThemeQuestion fetchThemeQuestion(String themeQuestionId){
        ThemeQuestion dbThemeQuestion = themeQuestionDAO.findById(themeQuestionId);
        if (null == dbThemeQuestion){
            throw new JSONException(Response.Status.NOT_FOUND, "No such theme question found", null);
        }
        return dbThemeQuestion;
    }

    @POST
    @Path("{applicationSystemId}/{learningOpportunityId}/{themeId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public void saveNewThemeQuestion(@PathParam("applicationSystemId") String applicationSystemId,
                                     @PathParam("learningOpportunityId") String learningOpportunityId,
                                     @PathParam("themeId")  String themeId,
                                     ThemeQuestion themeQuestion) {
        LOGGER.debug("Got " + themeQuestion);
        if (null == applicationSystemId || null == learningOpportunityId)
            throw new JSONException(Response.Status.BAD_REQUEST, "Missing pathparameters", null);
        String tqAsId = themeQuestion.getApplicationSystemId();
        if (! applicationSystemId.equals(tqAsId)) {
            themeQuestion.setApplicationSystemId(applicationSystemId);
            LOGGER.debug("Overriding given theme question application system id " + tqAsId + " with path param " + applicationSystemId);
        }
        String tqLoId = themeQuestion.getLearningOpportunityId();
        if (! learningOpportunityId.equals(tqLoId)) {
            themeQuestion.setLearningOpportunityId(learningOpportunityId);
            LOGGER.debug("Overriding given theme question learning opportunity id " + tqLoId + " with path param " + learningOpportunityId);
        }
        String tqThemeId = themeQuestion.getLearningOpportunityId();
        if (! themeId.equals(tqThemeId)) {
            themeQuestion.setTheme(themeId);
            LOGGER.debug("Overriding given theme question learning opportunity id " + tqThemeId + " with path param " + themeId);
        }
        LOGGER.debug("Saving Theme Question");
        themeQuestionDAO.save(themeQuestion);
        LOGGER.debug("Saved Theme Question");
    }

    @GET
    @Path("list/{applicationSystemId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public List<ThemeQuestion> getThemeQuestionQuery(@PathParam("applicationSystemId") String applicationSystemId,
      @QueryParam("aoId") String learningOpportunityId, @QueryParam("orgId") String organizationId){
        ThemeQuestionQueryParameters tqq = new ThemeQuestionQueryParameters();
        tqq.setApplicationSystemId(applicationSystemId);
        tqq.setLearningOpportunityId(learningOpportunityId);
        tqq.setOrganizationId(organizationId);
        List<ThemeQuestion> themeQuestions = themeQuestionDAO.query(tqq);
        return themeQuestions;
    }
}
