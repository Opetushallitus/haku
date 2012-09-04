package fi.vm.sade.oppija.haku.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDataCreator {
    public TestDataCreator() {
    }

    public Map<String, Object> createApplicationPeriod() {
        Map<String, Object> form = createForm();

        // application period that the form belongs to
        Map<String, Object> applicationPeriod = new HashMap<String, Object>();
        applicationPeriod.put("id", "yhteishaku");
        applicationPeriod.put("form", form);
        return applicationPeriod;
    }

    public Map<String, Object> createForm() {
        // input question
        Map<String, Object> question1 = new HashMap<String, Object>();
        question1.put("id", "question1");
        question1.put("type", "text");
        question1.put("description", "Etunimet");

        // help text question
        Map<String, Object> question2 = new HashMap<String, Object>();
        question2.put("id", "question2");
        question2.put("type", "HELP_TEXT");
        question2.put("related_question", "question1");
        question2.put("description", "Syötä etunimet kuten ne ovat passissa.");

        // first category
        Map<String, Object> category1 = new HashMap<String, Object>();
        category1.put("id", "OMATTIEDOT");
        List<Map<String, Object>> questions1 = new ArrayList<Map<String, Object>>();
        questions1.add(question1);
        questions1.add(question2);

        Map<String, Object> question4 = new HashMap<String, Object>();
        question4.put("id", "question3");
        question4.put("type", "checkbox");
        question4.put("description", "Pohjakoulutus");
        questions1.add(question4);
        category1.put("questions", questions1);
        // checkbox question
        Map<String, Object> question3 = new HashMap<String, Object>();
        question3.put("id", "question3");
        question3.put("type", "checkbox");
        question3.put("label", "Pohjakoulutus");
        List<String> options = new ArrayList<String>();
        options.add("Suomalainen ylioppilastutkinto");
        options.add("IB-tutkinto");
        options.add("EB-tutkinto");
        question3.put("options", options);

        // second category
        Map<String, Object> category2 = new HashMap<String, Object>();
        category2.put("id", "KOULUTUSTAUSTA");
        List<Map<String, Object>> questions2 = new ArrayList<Map<String, Object>>();
        questions2.add(question3);
        category2.put("questions", questions2);

        // the actual form
        Map<String, Object> form = new HashMap<String, Object>();
        form.put("id", "1234");
        List<Map<String, Object>> categories = new ArrayList<Map<String, Object>>();
        categories.add(category1);
        categories.add(category2);
        form.put("categories", categories);

        return form;
    }

}