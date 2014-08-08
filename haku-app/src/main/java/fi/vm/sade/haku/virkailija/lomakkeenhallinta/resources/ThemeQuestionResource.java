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

import com.google.common.collect.ImmutableList;
import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.resource.JSONException;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionQueryParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.haku.virkailija.organization.resource.OrganizationResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
@Path("/application-system-form-editor/theme-question")
public class ThemeQuestionResource {

    //NOTE: Supported roles ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD ROLE_APP_HAKULOMAKKEENHALLINTA_READ ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE
    public static final String CHARSET_UTF_8 = ";charset=UTF-8";
    private static final String PARAM_NEW_ORDINAL = "newOrdinal";
    private static final String PARAM_OLD_ORDINAL = "oldOrdinal";

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeQuestionResource.class);

    @Autowired
    private ThemeQuestionDAO themeQuestionDAO;
    @Autowired
    private HakukohdeService hakukohdeService;
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private HakuService hakuService;
    @Autowired
    private KoodistoService koodistoService;
    @Autowired
    private ApplicationOptionService applicationOptionService;

    public ThemeQuestionResource(){
    }

    @Autowired
    public ThemeQuestionResource(final ThemeQuestionDAO themeQuestionDAO,
                                 final HakukohdeService hakukohdeService,
                                 final OrganizationService organizationService,
                                 final HakuService hakuService,
                                 final KoodistoService koodistoService,
                                 final ApplicationOptionService applicationOptionService) {
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakukohdeService = hakukohdeService;
        this.organizationService = organizationService;
        this.hakuService = hakuService;
        this.koodistoService = koodistoService;
        this.applicationOptionService = applicationOptionService;
    }

    @GET
    @Path("{themeQuestionId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD', 'ROLE_APP_HAKULOMAKKEENHALLINTA_READ')")
    public ThemeQuestion getThemeQuestionByOid(@PathParam("themeQuestionId") String themeQuestionId) {
        LOGGER.debug("Getting question by Id: {}", themeQuestionId);
        return themeQuestionDAO.findById(themeQuestionId);
    }

    @GET
    @Path("{themeQuestionId}/generate")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD', 'ROLE_APP_HAKULOMAKKEENHALLINTA_READ')")
    public Element getGeneratedThemeQuestionByOid(@PathParam("themeQuestionId") String themeQuestionId) {
        LOGGER.debug("Getting question by Id: {}", themeQuestionId);
        ThemeQuestion themeQuestion = themeQuestionDAO.findById(themeQuestionId);
        FormParameters formParameters = new FormParameters(hakuService.getApplicationSystem(themeQuestion.getApplicationSystemId()), koodistoService, themeQuestionDAO, hakukohdeService, applicationOptionService);;
        return themeQuestion.generateElement(formParameters);
    }

    @DELETE
    @Path("{themeQuestionId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public void deleteThemeQuestionByOid(@PathParam("themeQuestionId") String themeQuestionId) {
        LOGGER.debug("Deleting theme question with id: {}", themeQuestionId);
        ThemeQuestion dbThemeQuestion = fetchThemeQuestion(themeQuestionId);
        dbThemeQuestion.setState(ThemeQuestion.State.DELETED);
        themeQuestionDAO.save(dbThemeQuestion);
        LOGGER.debug("ThemeQuestion {} saved with state {}", dbThemeQuestion.getId().toString(), dbThemeQuestion.getState());
    }

    @POST
    @Path("{themeQuestionId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public void updateThemeQuestion(@PathParam("themeQuestionId") String themeQuestionId,
                                    ThemeQuestion themeQuestion) {
        LOGGER.debug("Updating theme question with id: {}", themeQuestionId);

        if (!themeQuestionId.equals(themeQuestion.getId().toString())){
            throw new JSONException(Response.Status.BAD_REQUEST, "theme question id mismatch", null);
        }
        ThemeQuestion dbThemeQuestion = fetchThemeQuestion(themeQuestionId);

        themeQuestion = fillInOwnerOrganizationsFromApplicationOption(themeQuestion);

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
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public void saveNewThemeQuestion(@PathParam("applicationSystemId") String applicationSystemId,
                                     @PathParam("learningOpportunityId") String learningOpportunityId,
                                     @PathParam("themeId")  String themeId,
                                     ThemeQuestion themeQuestion) {
        LOGGER.debug("Posted " + themeQuestion);
        if (null == applicationSystemId || null == learningOpportunityId)
            throw new JSONException(Response.Status.BAD_REQUEST, "Missing pathparameters", null);
        String tqAsId = themeQuestion.getApplicationSystemId();
        if (! applicationSystemId.equals(tqAsId)) {
            throw new JSONException(Response.Status.BAD_REQUEST, "Data error: Mismatch on applicationSystemId from path and model", null);
        }
        String tqLoId = themeQuestion.getLearningOpportunityId();
        if (null == tqLoId){
            throw new JSONException(Response.Status.BAD_REQUEST, "Data error: Missing learningOpportunityId", null);
        }
        if (!learningOpportunityId.equals(tqLoId)) {
            throw new JSONException(Response.Status.BAD_REQUEST, "Data error: Mismatch on learningOpportunityId from path and model", null);
        }
        String tqThemeId = themeQuestion.getTheme();
        if (! themeId.equals(tqThemeId)) {
            throw new JSONException(Response.Status.BAD_REQUEST, "Data error: Mismatch on theme from path and model", null);
        }
        if (themeQuestion.getTargetIsGroup()) {
            themeQuestion = fillInOwnerOrganizationsFromApplicationOptionGroup(themeQuestion);
        } else {
            themeQuestion = fillInOwnerOrganizationsFromApplicationOption(themeQuestion);
        }
        LOGGER.debug("Saving Theme Question");
        themeQuestionDAO.save(themeQuestion);
        LOGGER.debug("Saved Theme Question");
    }

    @POST
    @Path("reorder/{learningOpportunityId}/{themeId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public void reorderThemeQuestions(@PathParam("learningOpportunityId") String learningOpportunityId,
      @PathParam("themeId") String themeId, Map<String,Map<String,String>> reorderedQuestions) {
        LOGGER.debug("Posted " + reorderedQuestions);
        if (null == learningOpportunityId || null == themeId)
            throw new JSONException(Response.Status.BAD_REQUEST, "Missing pathparameters", null);
        List<Map<String, String>> values = new ArrayList(reorderedQuestions.values());
        boolean changes = false;
        long ordinalCheckSum = 0;
        for (Map<String,String> value : values){
            try {
                Integer newOrdinal = Integer.valueOf(value.get(PARAM_NEW_ORDINAL));
                Integer oldOrdinal = Integer.valueOf(value.get(PARAM_OLD_ORDINAL));
                if (null == newOrdinal) {
                    LOGGER.debug("Exception due to new ordinal null value");
                    throw new JSONException(Response.Status.BAD_REQUEST, "New ordinal values are missing or not valid", null);
                }
                if (!newOrdinal.equals(oldOrdinal))
                    changes = true;
                if (newOrdinal < 1 || reorderedQuestions.size() < newOrdinal) {
                    LOGGER.debug("Exception due to new ordinal value. New ordinal: {}, questionCount : {}", newOrdinal, reorderedQuestions.size());
                    throw new JSONException(Response.Status.BAD_REQUEST, "New ordinal values are missing or not valid", null);
                }
                long currentOrdinalCheckSum = 1 << newOrdinal;
                if ((currentOrdinalCheckSum & ordinalCheckSum) > 0) {
                    LOGGER.debug("Exception due to ordinalCheckSums. Current: {}, total: {}", currentOrdinalCheckSum, ordinalCheckSum);
                    throw new JSONException(Response.Status.BAD_REQUEST, "Duplicate ordinals", null);
                }
                ordinalCheckSum += currentOrdinalCheckSum;
            } catch (NumberFormatException exception){
                throw new JSONException(Response.Status.BAD_REQUEST, "Ordinal values must be integers", null);
            }
        }
        if (!changes){
            LOGGER.debug("No changes. Skipping the rest");
            return;
        }
        //TODO =RS= some metalocking to simulate transactions or something

        Set<String> themeQuestionIds = reorderedQuestions.keySet();
        if (!themeQuestionDAO.validateLearningOpportunityAndTheme(learningOpportunityId, themeId,  themeQuestionIds.toArray(new String[themeQuestionIds.size()])))
            throw new JSONException(Response.Status.BAD_REQUEST, "Error in input data. Mismatch between question ids, theme and application option", null);
        // TODO =RS= do something if there are ordinals missing or old values do not match.
        for (String id : themeQuestionIds){
            Map<String, String> questionParam = reorderedQuestions.get(id);
            themeQuestionDAO.setOrdinal(id, Integer.valueOf(questionParam.get(PARAM_NEW_ORDINAL)));
        }
    }

    private ThemeQuestion fillInOwnerOrganizationsFromApplicationOption(ThemeQuestion themeQuestion){
        LOGGER.debug("Filling in organizations for theme question for application system " + themeQuestion.getApplicationSystemId() + " application option " + themeQuestion.getLearningOpportunityId());
        HakukohdeDTO applicationOption = null;
        try {
            applicationOption = hakukohdeService.findByOid(themeQuestion.getLearningOpportunityId());
            if (null == applicationOption)
                throw new JSONException(Response.Status.BAD_REQUEST, "Invalid learningOpportunityId", null);
        } catch (RuntimeException exception){
            LOGGER.error("Application Option Search failed", exception);
            throw new JSONException(Response.Status.BAD_REQUEST, "Invalid learningOpportunityId", null);
        }
        LOGGER.debug("Filling in organizations for theme question");
        String learningOpportunityProvicerId = applicationOption.getTarjoajaOid();
        List<String> parentOids = organizationService.findParentOids(learningOpportunityProvicerId);
        HashSet<String> ownerOrganizations = new HashSet<String>();
        ownerOrganizations.addAll(parentOids);
        ownerOrganizations.add(learningOpportunityProvicerId);
        themeQuestion.setOwnerOrganizationOids(new ArrayList<String>(ownerOrganizations));
        LOGGER.debug("Owner organizations "+ ownerOrganizations.toString() +" added for applicationoption "+ applicationOption.getOid());
        return themeQuestion;
    }

    private ThemeQuestion fillInOwnerOrganizationsFromApplicationOptionGroup(ThemeQuestion themeQuestion) {
        String applicationOptionGroupId = themeQuestion.getLearningOpportunityId();
        themeQuestion.setOwnerOrganizationOids(ImmutableList.of(OrganizationResource.ORGANIZATION_ROOT_ID, applicationOptionGroupId));
        return themeQuestion;
    }

    @GET
    @Path("list/{applicationSystemId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD', 'ROLE_APP_HAKULOMAKKEENHALLINTA_READ')")
    public List<ThemeQuestion> getThemeQuestionByQuery(@PathParam("applicationSystemId") String applicationSystemId,
      @QueryParam("aoId") String learningOpportunityId, @QueryParam("orgId") String organizationId, @QueryParam("themeId") String themeId){
        LOGGER.debug("Listing by applicationSystemId: {}, learningOpportunityId: {}, organizationId: {} ", applicationSystemId, applicationSystemId, organizationId);
        ThemeQuestionQueryParameters tqq = new ThemeQuestionQueryParameters();
        tqq.setApplicationSystemId(applicationSystemId);
        tqq.setLearningOpportunityId(learningOpportunityId);
        tqq.setOrganizationId(organizationId);
        tqq.setTheme(themeId);
        List<ThemeQuestion> themeQuestions = themeQuestionDAO.query(tqq);
        LOGGER.debug("Found {} ThemeQuestions", themeQuestions.size());
        return themeQuestions;
    }
}
