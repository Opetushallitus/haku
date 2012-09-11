package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import junit.framework.TestCase;

public class FormControllerTest extends TestCase {


    public void testGetForm() throws Exception {
        final String formId = "yhteishaku";
        final String firstCategoryId = "henkilotiedot";
        FormController formController = new FormController(new FormModelDummyMemoryDaoImpl(formId, firstCategoryId));
        String actual = formController.getForm("test", formId);
        String expected = "redirect:" + formId + "/" + firstCategoryId;
        assertEquals(expected, actual);
    }
    public void testGetFormNotFound() throws Exception {
        final String formId = "yhteishaku";
        final String firstCategoryId = "henkilotiedot";
        FormController formController = new FormController(new FormModelDummyMemoryDaoImpl(formId, firstCategoryId));
        String actual = formController.getForm("test", "väärä");
        String expected = "redirect:" + formId + "/" + firstCategoryId;
        assertEquals(expected, actual);
    }

    public void testGetCategory() throws Exception {

    }

    public void testSaveCategory() throws Exception {

    }
}
