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

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionQueryParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.SimpleObjectIdSerializer;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Controller
@Path("/lomakkeenhallinta/themequestion")
public class ThemeQuestionResource {

    public static final String CHARSET_UTF_8 = ";charset=UTF-8";

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeQuestionResource.class);


    private final ThemeQuestionDAO themeQuestionDAO;

    @Autowired
    public ThemeQuestionResource(ThemeQuestionDAO themeQuestionDAO) {
        this.themeQuestionDAO = themeQuestionDAO;
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
    @Path("{applicationSystemId}/{learningOpportunityProviderId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @JsonSerialize(using= SimpleObjectIdSerializer.class,
      as=ObjectId.class)
    public List<ThemeQuestion> getThemeQuestion(@PathParam("applicationSystemId") String applicationSystemId, @PathParam("learningOpportunityProviderId") String learningOpportunityProviderId) {
        ThemeQuestionQueryParameters tqq = new ThemeQuestionQueryParameters();
        tqq.setApplicationSystemId(applicationSystemId);
        tqq.setLearningOpportunityProviderId(learningOpportunityProviderId);
        List<ThemeQuestion> themeQuestions = themeQuestionDAO.query(tqq);
        return themeQuestions;
    }

    @GET
    @Path("{applicationSystemId}/{learningOpportunityProviderId}/{themeQuestionId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public ThemeQuestion getThemedQuestionByPath(@PathParam("themeQuestionId") String themeQuestionId) {
        LOGGER.debug("Getting question by: {}", themeQuestionId);
        return themeQuestionDAO.findById(themeQuestionId);
    }

    @POST
    @Path("{applicationSystemId}/{learningOpportunityProviderId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public void postThemeQuestion(@PathParam("applicationSystemId") String applicationSystemId,
                                    @PathParam("learningOpportunityProviderId") String learningOpportunityProviderId,
                                    ThemeQuestion themeQuestion) {
        LOGGER.debug("Got " + themeQuestion);
        themeQuestionDAO.save(themeQuestion);
    }
}
