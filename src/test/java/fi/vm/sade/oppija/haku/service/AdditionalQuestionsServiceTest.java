package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import fi.vm.sade.oppija.haku.domain.User;
import fi.vm.sade.oppija.haku.domain.questions.Question;
import fi.vm.sade.oppija.haku.service.impl.AdditionalQuestionServiceImpl;
import fi.vm.sade.oppija.haku.service.impl.HakemusServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

        List<Question> additionalQuestions = additionalQuestionService.findAdditionalQuestions(teemaId, hakemusId);

        assertNotNull(additionalQuestions);
        assertEquals(2, additionalQuestions.size());
    }

    @Test
    public void testEducationSpecificSubjects() {
        String teemaId = "arvosanatGrp";
        HakemusId hakemusId = new HakemusId("test", "yhteishaku", "arvosanat");

        List<Question> additionalQuestions = additionalQuestionService.findAdditionalQuestions(teemaId, hakemusId);

        assertNotNull(additionalQuestions);
        assertEquals(4, additionalQuestions.size());

    }

}
