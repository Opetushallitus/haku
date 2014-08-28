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
import fi.vm.sade.haku.oppija.hakemus.domain.util.ApplicationUtil;
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
import fi.vm.sade.haku.virkailija.koulutusinformaatio.KoulutusinformaatioService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.viestintapalvelu.PDFService;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOfficeDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UIServiceImpl implements UIService {

    private final ApplicationService applicationService;
    private final ApplicationSystemService applicationSystemService;
    private final String koulutusinformaatioBaseUrl;
    private final UserSession userSession;
    private final KoulutusinformaatioService koulutusinformaatioService;
    private final PDFService pdfService;

    @Autowired
    public UIServiceImpl(final ApplicationService applicationService,
                         final ApplicationSystemService applicationSystemService,
                         final UserSession userSession,
                         final KoulutusinformaatioService koulutusinformaatioService,
                         @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUrl, PDFService pdfService) {
        this.applicationService = applicationService;
        this.applicationSystemService = applicationSystemService;
        this.userSession = userSession;
        this.koulutusinformaatioService = koulutusinformaatioService;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUrl;
        this.pdfService = pdfService;
    }

    @Override
    public ModelResponse getCompleteApplication(final String applicationSystemId, final String oid) {
        ApplicationSystem activeApplicationSystem = applicationSystemService.getActiveApplicationSystem(applicationSystemId);
        Application application = applicationService.getSubmittedApplication(applicationSystemId, oid);
        List<String> discretionaryAttachmentAOIds = ApplicationUtil.getDiscretionaryAttachmentAOIds(application);
        Map<String, List<String>> higherEdAttachmentAOIds = ApplicationUtil.getHigherEdAttachmentAOIds(application);
        Map<String, List<fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO>> higherEdAttachments =
                new HashMap<String, List<fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO>>();
        for (Map.Entry<String, List<String>> entry : higherEdAttachmentAOIds.entrySet()) {
            String key = entry.getKey();
            List<fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationOptionDTO> aos =
                    new ArrayList<ApplicationOptionDTO>();
            for (String aoOid : entry.getValue()) {
                ApplicationOptionDTO ao = koulutusinformaatioService.getApplicationOption(aoOid);
                if (!addressAlreadyAdded(aos, ao)) {
                    aos.add(ao);
                }
            }
            higherEdAttachments.put(key, aos);
        }
        return new ModelResponse(application, activeApplicationSystem, discretionaryAttachmentAOIds,
                higherEdAttachments, koulutusinformaatioBaseUrl);
    }

    private boolean addressAlreadyAdded(List<ApplicationOptionDTO> aos, ApplicationOptionDTO ao) {
        if (aos.isEmpty()) {
            return false;
        }
        ApplicationOfficeDTO newOffice = ao.getProvider().getApplicationOffice();
        for (ApplicationOptionDTO currAo : aos) {
            ApplicationOfficeDTO currOffice = currAo.getProvider().getApplicationOffice();
            if (StringUtils.equals(newOffice.getName(), currOffice.getName())
                    && StringUtils.equals(newOffice.getPostalAddress().getStreetAddress(),
                        currOffice.getPostalAddress().getStreetAddress())
                    && StringUtils.equals(newOffice.getPostalAddress().getStreetAddress2(),
                        currOffice.getPostalAddress().getStreetAddress2())
                    && StringUtils.equals(newOffice.getPostalAddress().getPostalCode(),
                        currOffice.getPostalAddress().getPostalCode())
                    && StringUtils.equals(newOffice.getPostalAddress().getPostOffice(),
                        currOffice.getPostalAddress().getPostOffice())) {
                return true;
            }
        }
        return false;
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
        modelResponse.addAnswers(userSession.populateWithPrefillData(application.getVastauksetMerged()));
        modelResponse.setElement(phase);
        modelResponse.setKoulutusinformaatioBaseUrl(koulutusinformaatioBaseUrl);
        return modelResponse;
    }

    @Override
    public void storePrefilledAnswers(String applicationSystemId, Map<String, String> answers) {
        userSession.addPrefillData(applicationSystemId, answers);
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
    public ModelResponse savePhase(String applicationSystemId, String phaseId, Map<String, String> answers) {
        Form activeForm = applicationSystemService.getActiveApplicationSystem(applicationSystemId).getForm();
        ApplicationState applicationState = applicationService.saveApplicationPhase(
                new ApplicationPhase(applicationSystemId, phaseId, answers));
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
    public ModelResponse submitApplication(final String applicationSystemId) {
        ModelResponse modelResponse = new ModelResponse();
        modelResponse.setApplication(applicationService.submitApplication(applicationSystemId));
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
}
