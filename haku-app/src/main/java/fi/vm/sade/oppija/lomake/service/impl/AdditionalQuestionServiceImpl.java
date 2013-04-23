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

import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceRow;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceTable;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.domain.util.ElementUtil;
import fi.vm.sade.oppija.lomake.service.AdditionalQuestionService;
import fi.vm.sade.oppija.lomake.service.FormService;
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
    public Set<Question> findAdditionalQuestions(FormId formId, String phaseId, String themeId, String aoId,
                                                 Integer educationDegree, String preferenceRowId, Boolean sora) {

        Theme theme = findTheme(formId, phaseId, themeId);

        Set<Question> additionalQuestions = new LinkedHashSet<Question>();

        if (theme == null || theme.getAdditionalQuestions() == null) {
            return additionalQuestions;
        }

        if (preferenceRowId != null && !preferenceRowId.isEmpty()) {
            PreferenceTable table = ElementUtil.findElementsByTypeAsList(theme, PreferenceTable.class).get(0);
            PreferenceRow row = (PreferenceRow)table.getChildById(preferenceRowId);
            if (row != null && educationDegree.equals(table.getDiscretionaryEducationDegree())) {
                additionalQuestions.add(row.getDiscretionaryQuestion());
            }
        }

        List<Question> questions = theme.getAdditionalQuestions().get(aoId);
        if (questions != null && !questions.isEmpty()) {
            additionalQuestions.addAll(questions);
        }

        return additionalQuestions;
    }

    @Override
    public Question findDiscretionaryFollowUps(FormId formId, String phaseId, String themeId, String preferendeRowId) {
        Theme theme = findTheme(formId, phaseId, themeId);

        if (theme == null) {
            return null;
        }

        PreferenceRow row = (PreferenceRow)theme.getChildById(preferendeRowId);

        return row.getDiscretionaryQuestion().getFollowUp();
    }

    private Theme findTheme(FormId formId, String phaseId, String themeId) {
        Form form = formService.getActiveForm(formId.getApplicationPeriodId(), formId.getFormId());

        Theme theme = null;

        Element phase = form.getPhase(phaseId);
        for (Element e : phase.getChildren()) {
            if (e.getId().equals(themeId)) {
                theme = (Theme) e;
                break;
            }
        }
        return theme;
    }

}
