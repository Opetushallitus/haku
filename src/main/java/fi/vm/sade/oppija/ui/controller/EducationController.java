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

package fi.vm.sade.oppija.ui.controller;

import com.google.common.collect.Lists;
import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.ApplicationOption;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.Organization;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.service.AdditionalQuestionService;
import fi.vm.sade.oppija.lomake.service.ApplicationOptionService;
import fi.vm.sade.oppija.lomake.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller for education institute search
 *
 * @author Mikko Majapuro
 */
@Path(EducationController.EDUCATION_CONTROLLER_PATH)
@Controller
public class EducationController {

    public static final String TERM = "term";
    public static final String EDUCATION_CONTROLLER_PATH = "/education";

    @Qualifier("applicationOptionServiceSolrImpl")
    @Autowired
    ApplicationOptionService applicationOptionService;
    @Autowired
    @Qualifier("formServiceImpl")
    FormService formService;
    @Autowired
    ApplicationService applicationService;
    @Autowired
    AdditionalQuestionService additionalQuestionService;

    @GET
    @Path("/{hakuId}/organisaatio/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Organization> searchOrganisaatio(@PathParam("hakuId") final String hakuId,
                                                 @QueryParam(TERM) final String term) {
        return applicationOptionService.searchOrganisaatio(hakuId, term);
    }

    @GET
    @Path("/{hakuId}/hakukohde/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApplicationOption> searchHakukohde(@PathParam("hakuId") final String hakuId,
                                                   @QueryParam("organisaatioId") final String organisaatioId) {
        return applicationOptionService.searchHakukohde(hakuId, organisaatioId);
    }

    @GET
    @Path("/additionalquestion/{hakuId}/{lomakeId}/{vaiheId}/{teemaId}/{hakukohdeId}")
    @Produces(MediaType.TEXT_HTML)
    public Viewable getAdditionalQuestions(@PathParam("hakuId") final String hakuId,
                                           @PathParam("lomakeId") final String lomakeId,
                                           @PathParam("teemaId") final String teemaId,
                                           @PathParam("vaiheId") final String vaiheId,
                                           @PathParam("hakukohdeId") final String hakukohdeId,
                                           @QueryParam("preview") final boolean preview) {
        String viewName = preview ? "/additionalQuestionsPreview" : "/additionalQuestions";


        Form activeForm = formService.getActiveForm(hakuId, lomakeId);
        final FormId formId = new FormId(hakuId, activeForm.getId());
        Set<Question> additionalQuestions = additionalQuestionService.
                findAdditionalQuestions(teemaId, Lists.newArrayList(hakukohdeId), formId, vaiheId);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("additionalQuestions", additionalQuestions);
        model.put("categoryData", applicationService.getApplication(formId).getVastauksetMerged());
        return new Viewable(viewName, model);
    }
}
