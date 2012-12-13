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

package fi.vm.sade.oppija.lomake.service.impl;

import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Phase;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.service.AdditionalQuestionService;
import fi.vm.sade.oppija.lomake.service.FormService;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
@Service("additionalQuestionService")
public class AdditionalQuestionServiceImpl implements AdditionalQuestionService {

    FormService formService;
    ApplicationService applicationService;

    @Autowired
    public AdditionalQuestionServiceImpl(@Qualifier("formServiceImpl") FormService formService,
                                         @Qualifier("applicationServiceImpl") ApplicationService applicationService) {
        this.formService = formService;
        this.applicationService = applicationService;
    }

    @Override
    public Set<Question> findAdditionalQuestions(String teemaId, FormId formId, String vaiheId) {
        Map<String, String> hakemusValues = applicationService.getHakemus(formId).getVastauksetMerged();
        List<String> hakukohdeList = new ArrayList<String>();

        int prefNumber = 1;

        while (hakemusValues.containsKey("preference" + prefNumber + "-Koulutus-id")) {
            hakukohdeList.add(hakemusValues.get("preference" + prefNumber + "-Koulutus-id"));
            prefNumber++;
        }

        return findAdditionalQuestions(teemaId, hakukohdeList, formId, vaiheId);
    }

    @Override
    public Set<Question> findAdditionalQuestions(String teemaId, List<String> hakukohdeIds, FormId formId, final String vaiheId) {
        Theme theme = null;
        Form form = formService.getActiveForm(formId.getApplicationPeriodId(), formId.getFormId());
        Phase phase = form.getPhase(vaiheId);
        for (Element e : phase.getChildren()) {
            if (e.getId().equals(teemaId)) {
                theme = (Theme) e;
                break;
            }
        }

        Set<Question> additionalQuestions = new LinkedHashSet<Question>();

        if (theme == null || theme.getAdditionalQuestions() == null) {
            return additionalQuestions;
        }

        for (String hakukohdeId : hakukohdeIds) {
            List<Question> questions = theme.getAdditionalQuestions().get(hakukohdeId);
            if (questions != null && !questions.isEmpty()) {
                additionalQuestions.addAll(questions);
            }
        }

        return additionalQuestions;
    }
}
