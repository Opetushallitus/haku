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

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import fi.vm.sade.auditlog.User;
import fi.vm.sade.haku.HakuOperation;
import fi.vm.sade.haku.VirkailijaAuditLogger;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.hakemus.resource.JSONException;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionQueryParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestionCompact;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.FormConfigurationService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.service.ThemeQuestionConverter;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import org.bson.types.ObjectId;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

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
    private AuthenticationService authenticationService;
    @Autowired
    private FormConfigurationService formConfigurationService;
    @Autowired
    private ThemeQuestionConverter themeQuestionConverter;
    @Autowired
    private HakuService hakuService;

    @Autowired
    VirkailijaAuditLogger virkailijaAuditLogger;

    public ThemeQuestionResource(){
    }

    @Autowired
    public ThemeQuestionResource(final ThemeQuestionDAO themeQuestionDAO,
                                 final HakukohdeService hakukohdeService,
                                 final OrganizationService organizationService,
                                 final AuthenticationService authenticationService,
                                 final FormConfigurationService formConfigurationService,
                                 final HakuService hakuService,
                                 final VirkailijaAuditLogger virkailijaAuditLogger) {
        this.themeQuestionDAO = themeQuestionDAO;
        this.hakukohdeService = hakukohdeService;
        this.organizationService = organizationService;
        this.authenticationService = authenticationService;
        this.formConfigurationService = formConfigurationService;
        this.hakuService = hakuService;
        this.virkailijaAuditLogger = virkailijaAuditLogger;
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
        FormParameters formParameters = formConfigurationService.getFormParameters(
          themeQuestion.getApplicationSystemId());
        return themeQuestion.generateElement(formParameters);
    }

    @DELETE
    @Path("{themeQuestionId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public void deleteThemeQuestionByOid(@Context HttpServletRequest request,
                                         @PathParam("themeQuestionId") String themeQuestionId) {
        LOGGER.debug("Deleting theme question with id: {}", themeQuestionId);
        ThemeQuestion dbThemeQuestion = fetchThemeQuestion(themeQuestionId);
        if(!allowDeleteApplicationSystemThemeQuestions(dbThemeQuestion.getApplicationSystemId())) {
            throw new JSONException(FORBIDDEN, "theme question delete is not allowed", null);
        }
        if (themeQuestionHasActiveOrLockedChildren(themeQuestionId)) {
            throw new JSONException(BAD_REQUEST, "question.has.followup.questions.deletion.not.allowed", null);
        }
        themeQuestionDAO.delete(themeQuestionId);

        Target target = new Target.Builder()
                .setField("hakuOid", dbThemeQuestion.getApplicationSystemId())
                .setField("themeQuestionId", themeQuestionId)
                .setField("learningOpportunityId", dbThemeQuestion.getLearningOpportunityId())
                .setField("themeId", dbThemeQuestion.getTheme()).build();

        auditLogRequest(request, HakuOperation.APPSYS_THEMEQUESTION_DELETE, target);

        renumerateThemeQuestionOrdinals(dbThemeQuestion.getApplicationSystemId(), dbThemeQuestion.getLearningOpportunityId(), dbThemeQuestion.getTheme());
    }

    private boolean themeQuestionHasActiveOrLockedChildren(String themeQuestionId) {
        List<ThemeQuestion> dbThemeQuestionChildren = themeQuestionDAO.findByParentId(themeQuestionId);
        if (dbThemeQuestionChildren != null) {
            for (ThemeQuestion t : dbThemeQuestionChildren) {
                if (!t.getState().equals(ThemeQuestion.State.DELETED)) {
                    return true;
                }
            }
        }
        return false;
    }

    @POST
    @Path("{themeQuestionId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public void updateThemeQuestion(@Context HttpServletRequest request,
                                    @PathParam("themeQuestionId") String themeQuestionId,
                                    ThemeQuestion themeQuestion) throws IOException {
        LOGGER.debug("Updating theme question with id: {}", themeQuestionId);

        if (!themeQuestionId.equals(themeQuestion.getId().toString())){
            throw new JSONException(BAD_REQUEST, "theme question id mismatch", null);
        }
        if (themeQuestion.getTargetIsGroup()) {
            themeQuestion = fillInOwnerOrganizationsFromApplicationOptionGroup(themeQuestion);
        } else {
            themeQuestion = fillInOwnerOrganizationsFromApplicationOption(themeQuestion);
        }

        ThemeQuestion dbThemeQuestion = fetchThemeQuestion(themeQuestionId);

        if(!allowInsertOrModifyApplicationSystemThemeQuestions(dbThemeQuestion.getApplicationSystemId())) {
            throw new JSONException(FORBIDDEN, "theme question modify is not allowed", null);
        }

        themeQuestion.setCreatorPersonOid(dbThemeQuestion.getCreatorPersonOid());
        //Parent and ordinal cannot be changed here
        themeQuestion.setOrdinal(dbThemeQuestion.getOrdinal());
        themeQuestion.setParentId(dbThemeQuestion.getParentId());

        LOGGER.debug("Saving Theme Question with id: " + dbThemeQuestion.getId().toString());
        themeQuestionDAO.save(themeQuestion);
        LOGGER.debug("Saved Theme Question with id: " + themeQuestionId);

        Target target = new Target.Builder()
                .setField("hakuOid", dbThemeQuestion.getApplicationSystemId())
                .setField("themeQuestionId", themeQuestion.getId().toString())
                .setField("learningOpportunityId", dbThemeQuestion.getLearningOpportunityId())
                .setField("themeId", themeQuestion.getTheme()).build();


        auditLogRequest(request, HakuOperation.APPSYS_THEMEQUESTION_UPDATE, target);
    }

    private ThemeQuestion fetchThemeQuestion(String themeQuestionId){
        ThemeQuestion dbThemeQuestion = themeQuestionDAO.findById(themeQuestionId);
        if (null == dbThemeQuestion){
            throw new JSONException(NOT_FOUND, "No such theme question found", null);
        }
        return dbThemeQuestion;
    }

    @POST
    @Path("{applicationSystemId}/{learningOpportunityId}/{themeId}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD')")
    public void saveNewThemeQuestion(@Context HttpServletRequest request,
                                     @PathParam("applicationSystemId") String applicationSystemId,
                                     @PathParam("learningOpportunityId") String learningOpportunityId,
                                     @PathParam("themeId")  String themeId,
                                     ThemeQuestion themeQuestion) throws IOException {



        LOGGER.debug("Posted " + themeQuestion);
        if (null == applicationSystemId || null == learningOpportunityId)
            throw new JSONException(BAD_REQUEST, "Missing pathparameters", null);
        String tqAsId = themeQuestion.getApplicationSystemId();
        if (! applicationSystemId.equals(tqAsId)) {
            throw new JSONException(BAD_REQUEST, "Data error: Mismatch on applicationSystemId from path and model", null);
        }
        String tqLoId = themeQuestion.getLearningOpportunityId();
        if (null == tqLoId){
            throw new JSONException(BAD_REQUEST, "Data error: Missing learningOpportunityId", null);
        }
        if (!learningOpportunityId.equals(tqLoId)) {
            throw new JSONException(BAD_REQUEST, "Data error: Mismatch on learningOpportunityId from path and model", null);
        }
        String tqThemeId = themeQuestion.getTheme();
        if (! themeId.equals(tqThemeId)) {
            throw new JSONException(BAD_REQUEST, "Data error: Mismatch on theme from path and model", null);
        }
        if(!allowInsertOrModifyApplicationSystemThemeQuestions(applicationSystemId)) {
            throw new JSONException(FORBIDDEN, "theme question insert is not allowed", null);
        }
        //Check if parent exists
        String parentId = null;
        if (null != themeQuestion.getParentId()) {
            parentId = themeQuestion.getParentId().toString();
            if (!parentId.isEmpty()) {
                ThemeQuestion parent = themeQuestionDAO.findById(parentId);
                if (null == parent) {
                    throw new JSONException(BAD_REQUEST, "Data error: Parent does not exist", null);
                }
                if (parent.getState().equals(ThemeQuestion.State.DELETED)) {
                    throw new JSONException(BAD_REQUEST, "Data error: Parent is deleted", null);
                }
            }
        }
        if (themeQuestion.getTargetIsGroup()) {
            themeQuestion = fillInOwnerOrganizationsFromApplicationOptionGroup(themeQuestion);
        } else {
            themeQuestion = fillInOwnerOrganizationsFromApplicationOption(themeQuestion);
        }
        Person currentHenkilo = authenticationService.getCurrentHenkilo();
        themeQuestion.setCreatorPersonOid(currentHenkilo.getPersonOid());
        //Set ordinal
        if (null != parentId && !parentId.isEmpty()) { //Child
            Integer  maxOrdinal = themeQuestionDAO.getMaxOrdinalOfChildren(applicationSystemId, learningOpportunityId, themeId, parentId);
            LOGGER.debug("getMaxOrdinalOfChildren returned: " + maxOrdinal);
            themeQuestion.setOrdinal(null == maxOrdinal ? 1: maxOrdinal + 1 );
        } else {
            Integer maxOrdinal = themeQuestionDAO.getMaxOrdinal(applicationSystemId, learningOpportunityId, themeId);
            themeQuestion.setOrdinal(null == maxOrdinal ? 1 : maxOrdinal + 1);
        }

        LOGGER.debug("Saving Theme Question");
        themeQuestionDAO.save(themeQuestion);
        LOGGER.debug("Saved Theme Question");

        Target target = new Target.Builder()
                .setField("hakuOid", applicationSystemId)
                .setField("learningOpportunityId", learningOpportunityId)
                .setField("themeId", themeId).build();

        auditLogRequest(request, HakuOperation.APPSYS_THEMEQUESTION_INSERT, target);
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
            throw new JSONException(BAD_REQUEST, "Missing pathparameters", null);
        Set<String> themeQuestionIds = reorderedQuestions.keySet();

        if (!themeQuestionDAO.validateLearningOpportunityAndTheme(learningOpportunityId, themeId,  themeQuestionIds.toArray(new String[themeQuestionIds.size()])))
            throw new JSONException(BAD_REQUEST, "Error in input data. Mismatch between question ids, theme and application option", null);

        if (!themeQuestionHierarchyForReorderingValid(themeQuestionIds)) {
            LOGGER.debug("Exception due to invalid parentId values");
            throw new JSONException(BAD_REQUEST, "ParentId values of given questions invalid", null);
        }
        List<Map<String, String>> values = new ArrayList(reorderedQuestions.values());
        boolean changes = false;
        long ordinalCheckSum = 0;
        for (Map<String,String> value : values){
            try {
                Integer newOrdinal = Integer.valueOf(value.get(PARAM_NEW_ORDINAL));
                Integer oldOrdinal = Integer.valueOf(value.get(PARAM_OLD_ORDINAL));
                if (null == newOrdinal) {
                    LOGGER.debug("Exception due to new ordinal null value");
                    throw new JSONException(BAD_REQUEST, "New ordinal values are missing or not valid", null);
                }
                if (!newOrdinal.equals(oldOrdinal))
                    changes = true;
                if (newOrdinal < 1 || reorderedQuestions.size() < newOrdinal) {
                    LOGGER.debug("Exception due to new ordinal value. New ordinal: {}, questionCount : {}", newOrdinal, reorderedQuestions.size());
                    throw new JSONException(BAD_REQUEST, "New ordinal values are missing or not valid", null);
                }
                long currentOrdinalCheckSum = 1 << newOrdinal;
                if ((currentOrdinalCheckSum & ordinalCheckSum) > 0) {
                    LOGGER.debug("Exception due to ordinalCheckSums. Current: {}, total: {}", currentOrdinalCheckSum, ordinalCheckSum);
                    throw new JSONException(BAD_REQUEST, "Duplicate ordinals", null);
                }
                ordinalCheckSum += currentOrdinalCheckSum;
            } catch (NumberFormatException exception){
                throw new JSONException(BAD_REQUEST, "Ordinal values must be integers", null);
            }
        }
        if (!changes){
            LOGGER.debug("No changes. Skipping the rest");
            return;
        }
        //TODO =RS= some metalocking to simulate transactions or something

        //If there are ordinals missing or old values do not match, apply renumerate to fix integrity
        boolean ordinalIntegrityOk = true;
        for (String id : themeQuestionIds){
            Map<String, String> questionParam = reorderedQuestions.get(id);
            Integer dbOrdinal = themeQuestionDAO.findById(id).getOrdinal();
            if (dbOrdinal == null || !dbOrdinal.equals(Integer.valueOf(questionParam.get(PARAM_OLD_ORDINAL)))) {
                ordinalIntegrityOk = false;
            }
            themeQuestionDAO.setOrdinal(id, Integer.valueOf(questionParam.get(PARAM_NEW_ORDINAL)));
        }
        if(!ordinalIntegrityOk) {
            LOGGER.debug("Running ordinal renumeration to fix integrity");
            String applicationSystemId = themeQuestionDAO.findById(themeQuestionIds.iterator().next()).getApplicationSystemId();
            renumerateThemeQuestionOrdinals(applicationSystemId, learningOpportunityId, themeId);
        }
    }

    //Checks that to be reordered items are either all parents or all children of one parent
    private boolean themeQuestionHierarchyForReorderingValid(Set<String> themeQuestionIds) {
        if (!themeQuestionIds.isEmpty()) {
            ObjectId parentId = themeQuestionDAO.findById(themeQuestionIds.toArray()[0].toString()).getParentId();
            String firstItemParentId = null;
            if (null != parentId) {
                firstItemParentId = parentId.toString();
            }
            for (String id : themeQuestionIds) {
                parentId = themeQuestionDAO.findById(id).getParentId();
                if (isNotBlank(firstItemParentId)) { //Must be a child of same parent
                    if (null == parentId || isBlank(parentId.toString()) || !parentId.toString().equals(firstItemParentId)) {
                        return false;
                    }
                } else { //Must not be a child
                    if (null != parentId && isNotBlank(parentId.toString())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private ThemeQuestion fillInOwnerOrganizationsFromApplicationOption(final ThemeQuestion themeQuestion) throws IOException {
        LOGGER.debug("Filling in organizations for theme question for application system " + themeQuestion.getApplicationSystemId() + " application option " + themeQuestion.getLearningOpportunityId());
        HashSet<String> ownerOrganizations = new HashSet<String>();
        ownerOrganizations.addAll(fetchApplicationOptionParents(themeQuestion.getLearningOpportunityId()));
        themeQuestion.setOwnerOrganizationOids(new ArrayList<String>(ownerOrganizations));
        return themeQuestion;
    }

    private ThemeQuestion fillInOwnerOrganizationsFromApplicationOptionGroup(final ThemeQuestion themeQuestion) throws IOException {
        String applicationOptionGroupId = themeQuestion.getLearningOpportunityId();
        LOGGER.debug("Filling in organizations for theme question for application system " + themeQuestion.getApplicationSystemId() + " application option group " + applicationOptionGroupId);
        List <String> applicationOptionsIds = hakukohdeService.findByGroupAndApplicationSystem(applicationOptionGroupId, themeQuestion.getApplicationSystemId());
        HashSet<String> ownerOrganizations = new HashSet<String>();
        for (String applicationOptionId : applicationOptionsIds){
            ownerOrganizations.addAll(fetchApplicationOptionParents(applicationOptionId));
        }
        themeQuestion.setOwnerOrganizationOids(new ArrayList<String>(ownerOrganizations));
        return themeQuestion;
    }

    private List<String> fetchApplicationOptionParents(final String applicationOptionId) throws IOException {
        HakukohdeV1RDTO applicationOption = null;
        try {
            applicationOption = hakukohdeService.findByOid(applicationOptionId);
            if (null == applicationOption)
                throw new JSONException(BAD_REQUEST, "Invalid learningOpportunityId", null);
        } catch (RuntimeException exception){
            LOGGER.error("Application Option Search failed", exception);
            throw new JSONException(BAD_REQUEST, "Invalid learningOpportunityId", null);
        }
        LOGGER.debug("Filling in organizations for theme question");
        Iterator<String> providerOids = applicationOption.getTarjoajaOids().iterator();
        String learningOpportunityProvicerId = null;
        if (providerOids.hasNext()) {
            // TODO jossain vaiheessa täytyy hoitaa useampi provider
            learningOpportunityProvicerId = providerOids.next();
        }
        HashSet<String> parentOids = new HashSet<String>();
        parentOids.addAll(organizationService.findParentOids(learningOpportunityProvicerId));
        parentOids.add(learningOpportunityProvicerId);
        LOGGER.debug("Owner organizations " + parentOids.toString() + " fetched for applicationoption " + applicationOption.getOid());
        return new ArrayList<String>(parentOids);
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

    @GET
    @Path("allAsMap/{applicationSystemId}")
    @PreAuthorize("hasAnyRole('ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE', 'ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD', 'ROLE_APP_HAKULOMAKKEENHALLINTA_READ')")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public Response getAllThemeQuestionsAsMap(@PathParam("applicationSystemId") String applicationSystemId,
                                              @QueryParam("lang") String lang) {

        Set<String> validationErrors = getThemeQuestionsAsMapValidationErrors(applicationSystemId, lang);

        if (!validationErrors.isEmpty()) {
            return status(BAD_REQUEST)
                    .entity(ImmutableMap.of("errors", validationErrors))
                    .build();
        }

        Map<ObjectId, ThemeQuestionCompact> questionMap = new HashMap<>();

        ThemeQuestionQueryParameters queryParams = new ThemeQuestionQueryParameters();
        queryParams.setApplicationSystemId(applicationSystemId);

        for (ThemeQuestion question : themeQuestionDAO.query(queryParams)) {
            questionMap.put(question.getId(), themeQuestionConverter.convert(question, lang));
        }

        return ok(questionMap).build();
    }

    private boolean allowInsertOrModifyApplicationSystemThemeQuestions(String applicationSystemId) {
        ApplicationSystem applicationSystem = getApplicationSystem(applicationSystemId);
        if(isYhteishaku(applicationSystem.getHakutapa()) && isVarsinainenHaku(applicationSystem.getApplicationSystemType())) {
            return isRekisterinpitaja() || !isHakuaikaGoing(applicationSystem.getApplicationPeriods());
        }
        return true;
    }

    private boolean allowDeleteApplicationSystemThemeQuestions(String applicationSystemId) {
        ApplicationSystem applicationSystem = getApplicationSystem(applicationSystemId);
        if(isYhteishaku(applicationSystem.getHakutapa()) && isVarsinainenHaku(applicationSystem.getApplicationSystemType())) {
            return isRekisterinpitaja() || isBeforeFirstHakuaika(applicationSystem.getApplicationPeriods());
        }
        return true;
    }

    private boolean isHakuaikaGoing(List<ApplicationPeriod> applicationPeriods) {
        long now = System.currentTimeMillis();
        return applicationPeriods.stream().anyMatch(p -> p.getStart().getTime() <= now && now <= p.getEnd().getTime());
    }

    private boolean isBeforeFirstHakuaika(List<ApplicationPeriod> applicationPeriods) {
        return System.currentTimeMillis() < applicationPeriods.stream().mapToLong(p -> p.getStart().getTime()).min().getAsLong();
    }

    private ApplicationSystem getApplicationSystem(String applicationSystemId) {
        ApplicationSystem applicationSystem = hakuService.getApplicationSystem(applicationSystemId);
        if (applicationSystem == null)
            throw new JSONException(Response.Status.NOT_FOUND, "ApplicationSystem not found with id "+ applicationSystemId, null);
        return applicationSystem;
    }

    private boolean isYhteishaku(String hakutapa) {
        return "hakutapa_01".equals(hakutapa);
    }

    private boolean isVarsinainenHaku(String hakutyyppi) {
        return "hakutyyppi_01".equals(hakutyyppi);
    }

    private boolean isRekisterinpitaja() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
          .stream().map(GrantedAuthority::getAuthority).anyMatch(a -> isRekisterinpitajaRole(a));
    }

    private boolean isRekisterinpitajaRole(String role) {
        return "ROLE_APP_HAKULOMAKKEENHALLINTA_CRUD_1.2.246.562.10.00000000001".equals(role) ||
               "ROLE_APP_HAKULOMAKKEENHALLINTA_READ_UPDATE_1.2.246.562.10.00000000001".equals(role);
    }

    private static Set<String> getThemeQuestionsAsMapValidationErrors(String applicationSystemId, String lang) {
        Set<String> errors = new HashSet<>();
        if (isBlank(applicationSystemId)) {
            errors.add("applicationSystemId is required");
        }
        if (isBlank(lang)) {
            errors.add("lang is required");
        }
        return errors;
    }

    private void renumerateThemeQuestionOrdinals(final String applicationSystemId, final String applicationOptionId, final String themeId){
        // TODO: mutex
        //Fetch all theme questions for this application system/learning opportunity/theme as a sorted (ascending ordinal) list
        ThemeQuestionQueryParameters tqqp = new ThemeQuestionQueryParameters();
        tqqp.setApplicationSystemId(applicationSystemId);
        tqqp.setLearningOpportunityId(applicationOptionId);
        tqqp.setTheme(themeId);
        tqqp.addSortBy(ThemeQuestion.FIELD_ORDINAL, ThemeQuestionQueryParameters.SORT_ASCENDING);
        List<ThemeQuestion> allDbThemeQuestions = themeQuestionDAO.query(tqqp);
        //Renumerate ordinals, first the parents, then children one parent at a time
        if (!allDbThemeQuestions.isEmpty()) {
            List<ThemeQuestion> parentThemeQuestions = new ArrayList<ThemeQuestion>();
            List<ThemeQuestion> childThemeQuestions = new ArrayList<ThemeQuestion>();
            for (ThemeQuestion tq : allDbThemeQuestions) {
                if (tq.getParentId() == null) {
                    parentThemeQuestions.add(tq);
                } else {
                    childThemeQuestions.add(tq);
                }
            }
            //Parents
            processOrdinals(parentThemeQuestions);
            //Children
            if(!childThemeQuestions.isEmpty()) {
                for (ThemeQuestion parentTq : parentThemeQuestions) {
                    List<ThemeQuestion> childrenOfOneParent = new ArrayList<ThemeQuestion>();
                    for (ThemeQuestion childTq : childThemeQuestions) {
                        if (childTq.getParentId().toString().equals(parentTq.getId().toString())) {
                            childrenOfOneParent.add(childTq);
                        }
                    }
                    if (!childrenOfOneParent.isEmpty()) {
                        processOrdinals(childrenOfOneParent);
                    }
                }
            }
        }
    }

    private void processOrdinals(List<ThemeQuestion> tqList) {
        Integer assumedOrdinal = 1;
        List<ThemeQuestion> tqsWithoutOrdinal = new ArrayList<ThemeQuestion>();
        for (ThemeQuestion tq : tqList){
            if (null == tq.getOrdinal()){
                tqsWithoutOrdinal.add(tq);
            }
            if (!assumedOrdinal.equals(tq.getOrdinal())) {
                themeQuestionDAO.setOrdinal(tq.getId().toString(), assumedOrdinal);
            }
            assumedOrdinal++;
        }
        for (ThemeQuestion tq: tqsWithoutOrdinal){
            themeQuestionDAO.setOrdinal(tq.getId().toString(), assumedOrdinal++);
        }
    }

    private InetAddress getInetAddress(HttpServletRequest request) {
        InetAddress inetaddress;
        try {
            inetaddress = InetAddress.getByName(request.getRemoteAddr());
        } catch (UnknownHostException e) {
            LOGGER.error("Could not create inetaddress of remote address {}", request.getRemoteAddr());
            inetaddress = null;
        }
        return inetaddress;
    }

    private void auditLogRequest(HttpServletRequest request, HakuOperation operation, Target target) {
        auditLogRequest(request, operation, target, null);
    }

    private void auditLogRequest(HttpServletRequest request, HakuOperation operation, Target target, Changes changes) {
        if(changes == null) {
            changes = new Changes.Builder().build();
        }
        InetAddress inetaddress = getInetAddress(request);
        User user = new User(virkailijaAuditLogger.getCurrentPersonOid(), inetaddress, request.getSession().toString(), request.getHeader("user-agent"));
        virkailijaAuditLogger.log(user, operation, target, changes);
    }
}
