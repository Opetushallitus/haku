package fi.vm.sade.oppija.haku.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationPeriodDAOTest {

    @Autowired
    private ApplicationPeriodDAO applicationPeriodDAO;

    @Test
    public void testInsertApplicationPeriod() {

        Map<Object, Object> question1 = new HashMap<Object, Object>();
        question1.put("id", "question1");
        question1.put("type", "INPUT");
        question1.put("label", "Etunimet");

        Map<Object, Object> question2 = new HashMap<Object, Object>();
        question2.put("id", "question2");
        question2.put("type", "HELP_TEXT");
        question2.put("related_question", "question1");
        question2.put("text", "Syötä etunimet kuten ne ovat passissa.");


        Map<Object, Object> category1 = new HashMap<Object, Object>();
        category1.put("id", "OMATTIEDOT");
        List<Map<Object, Object>> questions = new ArrayList<Map<Object, Object>>();
        questions.add(question1);
        questions.add(question2);
        category1.put("questions", questions);

        Map<Object, Object> application = new HashMap<Object, Object>();
        application.put("id", "1234");
        List<Map<Object, Object>> categories = new ArrayList<Map<Object, Object>>();
        categories.add(category1);
        application.put("categories", categories);

        Map<Object, Object> applicationPeriod = new HashMap<Object, Object>();
        applicationPeriod.put("id", "YHTEISHAKU");
        applicationPeriod.put("application", application);

        applicationPeriodDAO.insert(applicationPeriod);

    }

}
