/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.oppija.ui.service.impl;

import com.google.common.base.Predicate;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.util.AttachmentUtil;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationState;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.gradegrid.GradeGrid;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.UserSession;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.ui.service.ModelResponse;
import fi.vm.sade.haku.oppija.ui.service.UIService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionAttachmentDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.OrganizationGroupDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class UIServiceImpl implements UIService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UIServiceImpl.class);

    private static final String PREFERENCE_PREFIX = "preference";
    private static final String OPTION_POSTFIX = "-Koulutus-id";
    private static final String OPTION_GROUP_POSTFIX = "-Koulutus-id-ao-groups";
    private static final String ATTACHMENT_GROUP_POSTFIX = "-Koulutus-id-attachmentgroups";
    private static final String ATTACHMENTS_POSTFIX = "-Koulutus-id-attachments";
    private static final String ATTACHMENT_GROUP_TYPE = "hakukohde_liiteosoite";

    private final ApplicationService applicationService;
    private final ApplicationSystemService applicationSystemService;
    private final String koulutusinformaatioBaseUrl;
    private final UserSession userSession;
    private final KoulutusinformaatioService koulutusinformaatioService;
    private final AuthenticationService authenticationService;
    private final PDFService pdfService;

    @Autowired
    public UIServiceImpl(final ApplicationService applicationService,
                         final ApplicationSystemService applicationSystemService,
                         final UserSession userSession,
                         final KoulutusinformaatioService koulutusinformaatioService,
                         final AuthenticationService authenticationService,
                         @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUrl, PDFService pdfService) {
        this.applicationService = applicationService;
        this.applicationSystemService = applicationSystemService;
        this.userSession = userSession;
        this.koulutusinformaatioService = koulutusinformaatioService;
        this.authenticationService = authenticationService;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUrl;
        this.pdfService = pdfService;
    }

    @Override
    public ModelResponse getCompleteApplication(final String applicationSystemId, final String oid) {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        Application application = applicationService.getSubmittedApplication(applicationSystemId, oid);

        return new ModelResponse(application, activeApplicationSystem,
                AttachmentUtil.resolveAttachments(activeApplicationSystem, application, koulutusinformaatioService),
                koulutusinformaatioBaseUrl);
    }

    @Override
    public ModelResponse getAllApplicationSystems(String... includeFields) {
        ModelResponse modelResponse = new ModelResponse();
        modelResponse.addObjectToModel(ModelResponse.APPLICATION_SYSTEMS,
                applicationSystemService.getAllApplicationSystems(includeFields));
        return modelResponse;
    }

    @Override
    public ModelResponse getPreview(String applicationSystemId) {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        Application application = applicationService.getApplication(applicationSystemId);
        ModelResponse modelResponse = new ModelResponse();
        modelResponse.addAnswers(application.getVastauksetMerged());
        modelResponse.setElement(activeApplicationSystem.getForm());
        return modelResponse;
    }

    @Override
    public ModelResponse getPhase(String applicationSystemId, String phaseId) {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        ElementTree elementTree = new ElementTree(activeApplicationSystem.getForm());
        Element phase = elementTree.getChildById(phaseId);
        Application application = applicationService.getApplication(applicationSystemId);
        elementTree.checkPhaseTransfer(application.getPhaseId(), phaseId);
        ModelResponse modelResponse = new ModelResponse(activeApplicationSystem);
        modelResponse.addAnswers(userSession.populateWithPrefillData(ensureGroupData(phaseId, application.getVastauksetMerged())));
        modelResponse.setElement(phase);
        modelResponse.setKoulutusinformaatioBaseUrl(koulutusinformaatioBaseUrl);
        return modelResponse;
    }

    private Map<String, String> ensureGroupData(String phaseId, Map<String, String> answers) {
        //TODO this is an evil kludge, pls kill it asap
        if (!OppijaConstants.PHASE_APPLICATION_OPTIONS.equals(phaseId))
            return answers;
        return ensureGroupData(answers);
    }

    private Map<String, String> ensureGroupData(Map<String, String> answers) {
        LOGGER.debug("Input map: " + answers.toString());
        Set<String> keys = new HashSet(answers.keySet());
        for (String key: keys){
            if (null != key
                    && key.startsWith(PREFERENCE_PREFIX)
                    && key.endsWith(OPTION_POSTFIX)
                    && isNotEmpty(answers.get(key))){
                String basekey = key.replace(OPTION_POSTFIX, "");
                String aoGroups = answers.get(basekey + OPTION_GROUP_POSTFIX);
                String attachmentGroups = answers.get(basekey + ATTACHMENT_GROUP_POSTFIX);
                String attachments = answers.get(basekey + ATTACHMENTS_POSTFIX);

                ApplicationOptionDTO applicationOption = null;
                if (isEmpty(aoGroups)
                        || isEmpty(attachmentGroups)) {
                    applicationOption = koulutusinformaatioService.getApplicationOption(answers.get(key));
                    List<OrganizationGroupDTO> organizationGroups = applicationOption.getOrganizationGroups();
                    if (null != organizationGroups && organizationGroups.size() > 0 ){
                        ArrayList<String> aoGroupList = new ArrayList<String>(organizationGroups.size());
                        ArrayList<String> attachmentGroupList = new ArrayList<String>();
                        for (OrganizationGroupDTO organizationGroup : organizationGroups) {
                            aoGroupList.add(organizationGroup.getOid());
                            if (organizationGroup.getGroupTypes().contains(ATTACHMENT_GROUP_TYPE)){
                                attachmentGroupList.add(organizationGroup.getOid());
                            }
                        }
                        answers.put(basekey + OPTION_GROUP_POSTFIX, StringUtils.join(aoGroupList, ","));
                        answers.put(basekey + ATTACHMENT_GROUP_POSTFIX, StringUtils.join(attachmentGroupList, ","));
                    }
                }

                if (isEmpty(attachments)) {
                    if (applicationOption == null) {
                        applicationOption = koulutusinformaatioService.getApplicationOption(answers.get(key));
                    }
                    List<ApplicationOptionAttachmentDTO> attachmentList = applicationOption.getAttachments();
                    if (attachmentList != null && !attachmentList.isEmpty()) {
                        answers.put(basekey + ATTACHMENTS_POSTFIX, "true");
                    }
                }
            }
        }
        LOGGER.debug("output map: " + answers.toString());
        return answers;
    }

    @Override
    public void storePrefilledAnswers(String applicationSystemId, Map<String, String> answers) {
        userSession.addPrefillData(applicationSystemId, ensureGroupData(answers));
    }

    @Override
    public Map<String, Object> getElementHelp(final String applicationSystemId, final String elementId, final Map<String, String> answers) {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        Application application = applicationService.getApplication(applicationSystemId);
        Map<String, Object> model = new HashMap<String, Object>();
        Map<String, String> vastauksetMerged = application.getVastauksetMerged();
        vastauksetMerged.putAll(answers);
        Element root = new ElementTree(activeApplicationSystem.getForm()).getChildById(elementId);
        List<Element> listOfTitledElements = ElementUtil.filterElements(root, new Predicate<Element>() {
            @Override
            public boolean apply(Element input) {
                return (input instanceof Titled);
            }
        },
                vastauksetMerged);
        model.put("theme", root); //why theme?
        model.put("listsOfTitledElements", listOfTitledElements);
        return model;
    }

    @Override
    public Map<String, Object> getAdditionalLanguageRow(String applicationSystemId, String gradeGridId) {
        Form activeForm = applicationSystemService.getActiveApplicationSystem(applicationSystemId).getForm();
        Element element = new ElementTree(activeForm).getChildById(gradeGridId);
        GradeGrid gradeGrid = (GradeGrid) element;
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(ModelResponse.ELEMENT, gradeGrid);
        model.put(ModelResponse.TEMPLATE, "gradegrid/additionalLanguageRow");
        return model;
    }

    @Override
    public ModelResponse updateRules(String applicationSystemId, String phaseId, String elementId, Map<String, String> currentAnswers) {
        Form activeForm = applicationSystemService.getActiveApplicationSystem(applicationSystemId).getForm();
        Application application = applicationService.getApplication(applicationSystemId);
        Map<String, String> values = application.getVastauksetMerged();
        for (String key : application.getPhaseAnswers(phaseId).keySet()) {
            values.remove(key);
        }
        values.putAll(currentAnswers);
        ModelResponse modelResponse = new ModelResponse();
        modelResponse.addAnswers(values);
        modelResponse.setElement(new ElementTree(activeForm).getChildById(elementId));
        modelResponse.setForm(activeForm);
        modelResponse.setApplicationSystemId(applicationSystemId);
        modelResponse.setKoulutusinformaatioBaseUrl(koulutusinformaatioBaseUrl);
        return modelResponse;
    }

    @Override
    public ModelResponse getPhaseElement(String applicationSystemId, String phaseId, String elementId) {
        Form activeForm = applicationSystemService.getActiveApplicationSystem(applicationSystemId).getForm();
        Application application = applicationService.getApplication(applicationSystemId);
        ElementTree elementTree = new ElementTree(activeForm);
        elementTree.checkPhaseTransfer(application.getPhaseId(), phaseId);
        ModelResponse modelResponse = new ModelResponse(application, activeForm, elementTree.getChildById(elementId));
        modelResponse.addAnswers(userSession.populateWithPrefillData(application.getVastauksetMerged()));
        modelResponse.setApplicationSystemId(applicationSystemId);
        modelResponse.setKoulutusinformaatioBaseUrl(koulutusinformaatioBaseUrl);
        return modelResponse;
    }

    @Override
    public ModelResponse savePhase(String applicationSystemId, String phaseId, Map<String, String> originalAnswers) {
        Map<String, String> ensuredAnswers = ensureGroupData(phaseId, originalAnswers);
        Form activeForm = applicationSystemService.getActiveApplicationSystem(applicationSystemId).getForm();
        ApplicationState applicationState = applicationService.saveApplicationPhase(
                new ApplicationPhase(applicationSystemId, phaseId, ensuredAnswers));
        ModelResponse modelResponse = new ModelResponse();
        modelResponse.setApplicationState(applicationState);
        if (!applicationState.isValid()) {
            modelResponse.setApplicationState(applicationState);
            modelResponse.setApplicationSystemId(applicationSystemId);
            modelResponse.setElement(new ElementTree(activeForm).getChildById(phaseId));
            modelResponse.setForm(activeForm);
            modelResponse.setKoulutusinformaatioBaseUrl(koulutusinformaatioBaseUrl);
        }
        return modelResponse;

    }

    @Override
    public ModelResponse submitApplication(final String applicationSystemId, String language) {
        ModelResponse modelResponse = new ModelResponse();
        modelResponse.setApplication(applicationService.submitApplication(applicationSystemId, language));
        return modelResponse;
    }

    @Override
    public ModelResponse getApplication(String applicationSystemId) {
        Application application = userSession.getApplication(applicationSystemId);
        if (application.isNew()) {
            Form activeForm = applicationSystemService.getActiveApplicationSystem(applicationSystemId).getForm();
            application.setPhaseId(ElementTree.getFirstChild(activeForm).getId());
        }
        return new ModelResponse(application);
    }

    @Override
    public HttpResponse getUriToPDF(String applicationSystemId, String oid) {

        Application application = applicationService.getSubmittedApplication(applicationSystemId, oid);
        if (application != null
                && application.getApplicationSystemId().equals(applicationSystemId)
                && application.getOid().equals(oid)) {
            String url = "/virkailija/hakemus/" + oid + "/print/view";
            return pdfService.getUriToPDF(url);
        }
        throw new ResourceNotFoundException("Not allowed");
    }

    @Override
    public String ensureLanguage(HttpServletRequest request, String applicationSystemId) {
        if (request == null) {
            return null;
        }
        Cookie langCookie = getLangCookie(request);
        if (langCookie == null || isBlank(langCookie.getValue())) {
            return null;
        }
        String lang = langCookie.getValue();
        ApplicationSystem as = applicationSystemService.getApplicationSystem(applicationSystemId);
        List<String> allowedLanguages = as.getAllowedLanguages();
        if (!allowedLanguages.contains(lang)) {
            lang = allowedLanguages.get(0);
            HttpSession session = request.getSession();
            Locale newLocale = new Locale(lang);
            Config.set(session, Config.FMT_LOCALE, newLocale);
            request.setAttribute("fi_vm_sade_oppija_language", lang);
        }
        return lang;
    }

    private Cookie getLangCookie(HttpServletRequest request) {
        String langCookie = authenticationService.getLangCookieName();
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(langCookie)) {
                return cookie;
            }
        }
        return null;
    }
}
