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

package fi.vm.sade.oppija.lomake.service;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.oppija.hakemus.service.impl.ApplicationServiceImpl;
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.service.impl.AdditionalQuestionServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hannu Lyytikainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@ActiveProfiles(profiles = "dev")
public class AdditionalQuestionsServiceTest {

    public static final FormId FORM_ID = new FormId("Yhteishaku", "yhteishaku");
    public static final User TESTUSER = new User("testuser");
    private AdditionalQuestionService additionalQuestionService;

    public AdditionalQuestionsServiceTest() {
        FormService formService = new FormServiceMockImpl();
        ApplicationService applicationService = createHakemusServiceMock();

        additionalQuestionService = new AdditionalQuestionServiceImpl(formService, applicationService);

    }

    private ApplicationService createHakemusServiceMock() {
        ApplicationService applicationService = mock(ApplicationServiceImpl.class);
        Map<String, String> values = new HashMap<String, String>();
        values.put("preference1-Koulutus-id", "1.2.246.562.14.79893512065");
        Application application = new Application(FORM_ID, TESTUSER);
        application.addVaiheenVastaukset("hakutoiveet", values);

        when(applicationService.getApplication(FORM_ID)).thenReturn(application);

        return applicationService;
    }

    @Test
    public void testEducationSpecificQuestions() {
        String teemaId = "hakutoiveetGrp";
        Set<Question> additionalQuestions = additionalQuestionService.findAdditionalQuestions(teemaId, FORM_ID, "hakutoiveet");
        assertEquals(3, additionalQuestions.size());
    }

    @Test
    public void testEducationSpecificSubjects() {
        String teemaId = "arvosanatGrp";
        Set<Question> additionalQuestions = additionalQuestionService.findAdditionalQuestions(teemaId, FORM_ID, "arvosanat");
        assertEquals(2, additionalQuestions.size());

    }

}
