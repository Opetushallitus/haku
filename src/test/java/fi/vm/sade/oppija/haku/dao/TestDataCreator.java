package fi.vm.sade.oppija.haku.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDataCreator {
    public TestDataCreator() {
    }

    public Map<String, Object> createForm() {
        // input question
        Map<String, Object> question1 = new HashMap<String, Object>();
        question1.put("id", "question1");
        question1.put("type", "INPUT");
        question1.put("label", "Etunimet");

        // help text question
        Map<String, Object> question2 = new HashMap<String, Object>();
        question2.put("id", "question2");
        question2.put("type", "HELP_TEXT");
        question2.put("related_question", "question1");
        question2.put("text", "Syötä etunimet kuten ne ovat passissa.");

        // checkbox question
        Map<String, Object> question3 = new HashMap<String, Object>();
        question3.put("id", "question3");
        question3.put("type", "CHECKBOX");
        question3.put("label", "Pohjakoulutus");
        question3.put("option1", "Suomalainen ylioppilastutkinto");
        question3.put("option2", "IB-tutkinto");
        question3.put("option3", "EB-tutkinto");

        Map<String, Object> category1 = new HashMap<String, Object>();
        category1.put("id", "OMATTIEDOT");
        List<Map<String, Object>> questions = new ArrayList<Map<String, Object>>();
        questions.add(question1);
        questions.add(question2);
        questions.add(question3);
        category1.put("questions", questions);

        Map<String, Object> application = new HashMap<String, Object>();
        application.put("id", "1234");
        List<Map<String, Object>> categories = new ArrayList<Map<String, Object>>();
        categories.add(category1);
        application.put("categories", categories);

        Map<String, Object> applicationPeriod = new HashMap<String, Object>();
        applicationPeriod.put("id", "YHTEISHAKU");
        applicationPeriod.put("application", application);
        return applicationPeriod;
    }
}