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

package fi.vm.sade.oppija.ui.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.oppija.ui.service.UIService;
import fi.vm.sade.oppija.ui.service.UIServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    public static final Logger LOGGER = LoggerFactory.getLogger(UIServiceImpl.class);


    @Autowired
    public UIServiceImpl(final ApplicationService applicationService, final FormService formService,
                         @Value("${koulutusinformaatio.base.url}") final String koulutusinformaatioBaseUrl) {
        this.applicationService = applicationService;
        this.formService = formService;
        this.koulutusinformaatioBaseUrl = koulutusinformaatioBaseUrl;
    }


    @Override
    public UIServiceResponse getApplicationPrint(String oid) throws ResourceNotFoundException {
        Application application = applicationService.getApplication(oid);
        final Form activeForm = formService.getForm(application.getFormId());
        ApplicationPrintViewResponse response = new ApplicationPrintViewResponse();
        response.setApplication(application);
        response.setForm(activeForm);
        response.setDiscretionaryAttachmentAOIds(getDiscretionaryAttachmentAOIds(application));
        response.addObjectToModel("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);
        return response;
    }

    @Override
    public UIServiceResponse getApplicationPrint(String applicationPeriodId, String formId, String oid) throws ResourceNotFoundException {
        Form activeForm = formService.getActiveForm(applicationPeriodId, formId);
        final FormId hakuLomakeId = new FormId(applicationPeriodId, activeForm.getId());
        Application application = applicationService.getPendingApplication(hakuLomakeId, oid);
        ApplicationPrintViewResponse response = new ApplicationPrintViewResponse();
        response.setApplication(application);
        response.setForm(activeForm);
        response.setDiscretionaryAttachmentAOIds(getDiscretionaryAttachmentAOIds(application));
        response.addObjectToModel("koulutusinformaatioBaseUrl", koulutusinformaatioBaseUrl);
        return response;
    }

    private List<String> getDiscretionaryAttachmentAOIds(final Application application) {
        //AOs requiring attachments
        List<String> discretionaryAttachmentAOs = Lists.newArrayList();
        Map<String, String> answers = application.getVastauksetMerged();
        int i = 1;
        while(true) {
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
