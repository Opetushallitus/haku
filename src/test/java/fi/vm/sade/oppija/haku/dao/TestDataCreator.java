package fi.vm.sade.oppija.haku.dao;

import java.util.*;

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
        Map<String, Object> questionLastname = createTextQuestion("Sukunimi");

        // first name question
        Map<String, Object> questionFirstname = createTextQuestion("Etunimet");

        // sex question
        Map<String, Object> questionSex = createRadioGroup("Sukupuoli", "mies", "nainen");

        // personal info question group
        Map<String, Object> questionGroupPersonalInfo = createQuestionGroup("Henkilötiedot", questionFirstname, questionLastname, questionSex);

        // first category
        Map<String, Object> categoryPersonalInfo = createCategory(questionGroupPersonalInfo, "OMATTIEDOT");

        //  background question group
        Map<String, Object> questionGroupBackground = createQuestionGroup("Koulutustausta", createTextQuestion("Minulla on tutkinto seuraavasta oppilaitoksesta"), createTextQuestion("Joku muu, mikä?"));

        // second category
        Map<String, Object> categoryBackground = createCategory(questionGroupBackground, "KOULUTUSTAUSTA");

        // the actual form

        return createForm("1234", categoryPersonalInfo, categoryBackground);
    }

    private Map<String, Object> createForm(String id, Map<String, Object>... categories) {
        Map<String, Object> form = new HashMap<String, Object>();
        form.put("id", id);
        form.put("categories", Arrays.asList(categories));
        return form;
    }

    private Map<String, Object> createCategory(Map<String, Object> questionGroup, String id) {
        Map<String, Object> categoryPersonalInfo = new HashMap<String, Object>();
        categoryPersonalInfo.put("id", id);
        List<Map<String, Object>> questionGroupsPersonalInfo = new ArrayList<Map<String, Object>>();
        questionGroupsPersonalInfo.add(questionGroup);
        categoryPersonalInfo.put("questionGroups", questionGroupsPersonalInfo);
        return categoryPersonalInfo;
    }

    private Map<String, Object> createQuestionGroup(String title, Map<String, Object>... questions) {
        Map<String, Object> questionGroupPersonalInfo = new HashMap<String, Object>();
        questionGroupPersonalInfo.put("title", title);
        questionGroupPersonalInfo.put("questions", Arrays.asList(questions));
        return questionGroupPersonalInfo;
    }

    private Map<String, Object> createRadioGroup(String description, String... options) {
        Map<String, Object> questionSex = new HashMap<String, Object>();
        questionSex.put("id", createId(description));
        questionSex.put("description", description);
        questionSex.put("type", "radiogroup");
        addOptions(questionSex, options);
        return questionSex;
    }

    private void addOptions(Map<String, Object> question, String... options) {
        List<Map<String, Object>> questionOptions = new ArrayList<Map<String, Object>>();
        for (String option : options) {
            addOption(questionOptions, option);
        }
        question.put("options", questionOptions);
    }

    private void addOption(List<Map<String, Object>> sexQuestionOptions, String description) {
        Map<String, Object> sexMaleOption = new HashMap<String, Object>();
        sexMaleOption.put("description", description);
        sexMaleOption.put("id", description);
        sexQuestionOptions.add(sexMaleOption);
    }

    private Map<String, Object> createTextQuestion(String sukunimi) {
        return createQuestion(sukunimi, "input");
    }

    private Map<String, Object> createQuestion(String description, String type) {
        Map<String, Object> questionLastname = new HashMap<String, Object>();
        questionLastname.put("id", createId(description));
        questionLastname.put("type", type);
        questionLastname.put("description", description);
        return questionLastname;
    }

    private String createId(String description) {
        return description + "_" + System.currentTimeMillis();
    }

}