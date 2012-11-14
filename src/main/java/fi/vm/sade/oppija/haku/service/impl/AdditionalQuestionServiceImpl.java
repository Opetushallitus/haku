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

package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Teema;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.elements.questions.Question;
import fi.vm.sade.oppija.haku.service.AdditionalQuestionService;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.service.HakemusService;
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
    HakemusService hakemusService;

    @Autowired
    public AdditionalQuestionServiceImpl(@Qualifier("formServiceImpl") FormService formService,
                                         @Qualifier("hakemusServiceImpl") HakemusService hakemusService) {
        this.formService = formService;
        this.hakemusService = hakemusService;
    }

    @Override
    public Set<Question> findAdditionalQuestions(String teemaId, HakemusId hakemusId, String vaiheId) {
        Map<String, String> hakemusValues = hakemusService.getHakemus(hakemusId).getVastaukset();
        List<String> hakukohdeList = new ArrayList<String>();

        int prefNumber = 1;

        while (hakemusValues.containsKey("preference" + prefNumber + "-Koulutus-id")) {
            hakukohdeList.add(hakemusValues.get("preference" + prefNumber + "-Koulutus-id"));
            prefNumber++;
        }

        return findAdditionalQuestions(teemaId, hakukohdeList, hakemusId, vaiheId);
    }

    @Override
    public Set<Question> findAdditionalQuestions(String teemaId, List<String> hakukohdeIds, HakemusId hakemusId, final String vaiheId) {
        Teema teema = null;
        Form form = formService.getActiveForm(hakemusId.getApplicationPeriodId(), hakemusId.getFormId());
        Vaihe vaihe = form.getCategory(vaiheId);
        for (Element e : vaihe.getChildren()) {
            if (e.getId().equals(teemaId)) {
                teema = (Teema) e;
                break;
            }
        }

        Set<Question> additionalQuestions = new LinkedHashSet<Question>();

        if (teema == null || teema.getAdditionalQuestions() == null) {
            return additionalQuestions;
        }

        for (String hakukohdeId : hakukohdeIds) {
            List<Question> questions = teema.getAdditionalQuestions().get(hakukohdeId);
            if (questions != null && !questions.isEmpty()) {
                additionalQuestions.addAll(questions);
            }
        }

        return additionalQuestions;
    }

    @Override
    public Map<String, Set<Question>> findAdditionalQuestionsInCategory(final HakemusId hakemusId, final String vaiheId) {
        Form form = formService.getActiveForm(hakemusId.getApplicationPeriodId(), hakemusId.getFormId());
        Vaihe vaihe = form.getCategory(vaiheId);
        Map<String, Set<Question>> questionMap = new HashMap<String, Set<Question>>();

        if (vaihe != null) {
            for (Element e : vaihe.getChildren()) {
                questionMap.put(e.getId(), findAdditionalQuestions(e.getId(), hakemusId, vaiheId));
            }
        }
        return questionMap;
    }
}
