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

package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.User;
import fi.vm.sade.oppija.haku.domain.elements.questions.Question;
import fi.vm.sade.oppija.haku.service.impl.AdditionalQuestionServiceImpl;
import fi.vm.sade.oppija.haku.service.impl.HakemusServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")

public class AdditionalQuestionsServiceTest {

    private AdditionalQuestionService additionalQuestionService;

    public AdditionalQuestionsServiceTest() {
        FormService formService = new FormModelDummyMemoryDaoImpl();
        HakemusService hakemusService = createHakemusServiceMock();

        additionalQuestionService = new AdditionalQuestionServiceImpl(formService, hakemusService);

    }

    private HakemusService createHakemusServiceMock() {
        HakemusService hakemusService = mock(HakemusServiceImpl.class);

        HakemusId hakemusId = new HakemusId("test", "yhteishaku", "arvosanat");

        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Koulutus-id", "0_0");

        Hakemus hakemus = new Hakemus(hakemusId, values, new User("testuser"));

        when(hakemusService.getHakemus(hakemusId)).thenReturn(hakemus);

        return hakemusService;
    }

    @Test
    public void testEducationSpecificQuestions() {

        String teemaId = "hakutoiveetGrp";
        HakemusId hakemusId = new HakemusId("test", "yhteishaku", "hakutoiveet");

        Set<Question> additionalQuestions = additionalQuestionService.findAdditionalQuestions(teemaId, hakemusId);

        assertNotNull(additionalQuestions);
        assertEquals(3, additionalQuestions.size());
    }

    @Test
    public void testEducationSpecificSubjects() {
        String teemaId = "arvosanatGrp";
        HakemusId hakemusId = new HakemusId("test", "yhteishaku", "arvosanat");

        Set<Question> additionalQuestions = additionalQuestionService.findAdditionalQuestions(teemaId, hakemusId);

        assertNotNull(additionalQuestions);
        assertEquals(4, additionalQuestions.size());

    }

}
