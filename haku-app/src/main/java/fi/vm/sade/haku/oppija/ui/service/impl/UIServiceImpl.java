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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.haku.oppija.lomake.service.FormService;
import fi.vm.sade.haku.oppija.lomake.util.ElementTree;
import fi.vm.sade.haku.oppija.ui.service.UIService;
import fi.vm.sade.haku.oppija.ui.service.ModelResponse;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
@Service
public class UIServiceImpl implements UIService {

    private final ApplicationService applicationService;
    private final FormService formService;
    private final String koulutusinformaatioBaseUrl;


    @Autowired
    public UIServiceImpl(final ApplicationService applicationService, final FormService formService,
                         @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUrl) {
        this.applicationService = applicationService;
        this.formService = formService;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUrl;
    }


    @Override
    public ModelResponse getApplicationPrint(final String oid) throws ResourceNotFoundException {
        Application application = applicationService.getApplicationByOid(oid);
        final Form activeForm = formService.getForm(application.getApplicationSystemId());
        List<String> discretionaryAttachmentAOIds = getDiscretionaryAttachmentAOIds(application);
        return new ModelResponse(application, activeForm, discretionaryAttachmentAOIds, koulutusinformaatioBaseUrl);
    }

    @Override
    public ModelResponse getApplicationPrint(final String applicationSystemId, final String oid) throws ResourceNotFoundException {
        Form activeForm = formService.getActiveForm(applicationSystemId);
        Application application = applicationService.getSubmittedApplication(applicationSystemId, oid);
        List<String> discretionaryAttachmentAOIds = getDiscretionaryAttachmentAOIds(application);
        return new ModelResponse(application, activeForm, discretionaryAttachmentAOIds, koulutusinformaatioBaseUrl);
    }

    @Override
    public ModelResponse getApplicationComplete(final String applicationSystemId, final String oid) throws ResourceNotFoundException {
        Form activeForm = formService.getActiveForm(applicationSystemId);
        Application application = applicationService.getPendingApplication(applicationSystemId, oid);
        List<String> discretionaryAttachmentAOIds = getDiscretionaryAttachmentAOIds(application);
        ModelResponse response = new ModelResponse(application, activeForm, discretionaryAttachmentAOIds, koulutusinformaatioBaseUrl);
        response.setApplicationCompleteElements(formService.getApplicationCompleteElements(applicationSystemId));
        return response;
    }

    @Override
    public Map<String, Object> getElementHelp(final String applicationSystemId, final String elementId) throws ResourceNotFoundException {
        Form activeForm = formService.getActiveForm(applicationSystemId);
        Map<String, Object> model = new HashMap<String, Object>();
        Element theme = new ElementTree(activeForm).getChildById(elementId);
        model.put("theme", theme);
        List<Element> listsOfTitledElements = new ArrayList<Element>();
        for (Element tElement : theme.getChildren()) {
            if (tElement instanceof Titled) {
                listsOfTitledElements.add(tElement);
            }
        }
        listsOfTitledElements.add(theme);
        model.put("listsOfTitledElements", listsOfTitledElements);
        return model;
    }

    private List<String> getDiscretionaryAttachmentAOIds(final Application application) {
        //AOs requiring attachments
        List<String> discretionaryAttachmentAOs = Lists.newArrayList();
        Map<String, String> answers = application.getVastauksetMerged();
        int i = 1;
        while (true) {
            String key = String.format(OppijaConstants.PREFERENCE_ID, i);
            if (answers.containsKey(key)) {
                String aoId = answers.get(key);
                String discretionaryKey = String.format(OppijaConstants.PREFERENCE_DISCRETIONARY, i);
                if (!Strings.isNullOrEmpty(aoId) && answers.containsKey(discretionaryKey)) {
                    String discretionaryValue = answers.get(discretionaryKey);
                    if (!Strings.isNullOrEmpty(discretionaryValue) && Boolean.parseBoolean(discretionaryValue)) {
                        discretionaryAttachmentAOs.add(aoId);
                    }
                }
            } else {
                break;
            }
            ++i;
        }
        return discretionaryAttachmentAOs;
    }
}
