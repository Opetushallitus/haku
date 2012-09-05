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
        // last name question
        Map<String, Object> questionLastname = new HashMap<String, Object>();
        questionLastname.put("id", "lastname");
        questionLastname.put("type", "input");
        questionLastname.put("description", "Sukunimi");

        // first name question
        Map<String, Object> questionFirstname = new HashMap<String, Object>();
        questionFirstname.put("id", "firstname");
        questionFirstname.put("type", "input");
        questionFirstname.put("description", "Etunimet");

        // sex question
        Map<String, Object> questionSex = new HashMap<String, Object>();
        questionSex.put("id", "sex");
        questionSex.put("description", "Sukupuoli");
        questionSex.put("type", "radiogroup");
        Map<String, Object> sexMaleOption = new HashMap<String, Object>();
        sexMaleOption.put("id", "male");
        sexMaleOption.put("description", "mies");
        Map<String, Object> sexFemaleOption = new HashMap<String, Object>();
        sexFemaleOption.put("id", "female");
        sexFemaleOption.put("description", "nainen");
        List<Map<String, Object>> sexQuestionOptions = new ArrayList<Map<String, Object>>();
        sexQuestionOptions.add(sexMaleOption);
        sexQuestionOptions.add(sexFemaleOption);
        questionSex.put("options", sexQuestionOptions);

        // personal info question group
        Map<String, Object> questionGroupPersonalInfo = new HashMap<String, Object>();
        questionGroupPersonalInfo.put("title", "Henkilötiedot");
        List<Map<String, Object>> questionsPersonalInfo = new ArrayList<Map<String, Object>>();
        questionsPersonalInfo.add(questionLastname);
        questionsPersonalInfo.add(questionFirstname);
        questionsPersonalInfo.add(questionSex);
        questionGroupPersonalInfo.put("questions", questionsPersonalInfo);


        // first category
        Map<String, Object> categoryPersonalInfo = new HashMap<String, Object>();
        categoryPersonalInfo.put("id", "OMATTIEDOT");
        List<Map<String, Object>> questionGroupsPersonalInfo = new ArrayList<Map<String, Object>>();
        questionGroupsPersonalInfo.add(questionGroupPersonalInfo);
        categoryPersonalInfo.put("questionGroups", questionGroupsPersonalInfo);



        //  background question group
        Map<String, Object> questionGroupBackground = new HashMap<String, Object>();
        questionGroupBackground.put("title", "Koulutustausta");
        List<Map<String, Object>> questionsBackground = new ArrayList<Map<String, Object>>();

        questionGroupBackground.put("questions", questionsPersonalInfo);

        // second category
        Map<String, Object> categoryBackground = new HashMap<String, Object>();
        categoryBackground.put("id", "KOULUTUSTAUSTA");
        List<Map<String, Object>> questionGroupsBackground = new ArrayList<Map<String, Object>>();
        questionGroupsBackground.add(questionGroupBackground);
        categoryBackground.put("questionsGroups", questionGroupsBackground);

        // the actual form
        Map<String, Object> form = new HashMap<String, Object>();
        form.put("id", "1234");
        List<Map<String, Object>> categories = new ArrayList<Map<String, Object>>();
        categories.add(categoryPersonalInfo);
        categories.add(categoryBackground);
        form.put("categories", categories);

        return form;
    }

}