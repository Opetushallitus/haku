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

import fi.vm.sade.oppija.lomake.domain.elements.Form;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.service.AdditionalQuestionService;
import fi.vm.sade.oppija.lomake.service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service("additionalQuestionService")
public class AdditionalQuestionServiceImpl implements AdditionalQuestionService {

    public static final Logger LOGGER = LoggerFactory.getLogger(AdditionalQuestionServiceImpl.class);

    FormService formService;

    @Autowired
    public AdditionalQuestionServiceImpl(@Qualifier("formServiceImpl") FormService formService) {
        this.formService = formService;
    }

    @Override
    public List<Question> findAdditionalQuestions(final String applicationPeriodId, final String themeId, final String aoId) {
        Form form = formService.getActiveForm(applicationPeriodId);
        Theme theme = (Theme) form.getChildById(themeId);
        return theme.getAdditionalQuestions(aoId);

    }
}
