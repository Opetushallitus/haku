package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class FormControllerTest {

    @Test
    public void testGetForm() throws Exception {
        final String formId = "yhteishaku";
        final String firstCategoryId = "henkilotiedot";
        FormController formController = new FormController(new FormModelDummyMemoryDaoImpl(formId, firstCategoryId));
        String actual = formController.getForm("test", formId);
        String expected = "redirect:" + formId + "/" + firstCategoryId;
        assertEquals(expected, actual);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFormNotFound() throws Exception {
        final String formId = "yhteishaku";
        final String firstCategoryId = "henkilotiedot";
        FormController formController = new FormController(new FormModelDummyMemoryDaoImpl(formId, firstCategoryId));
        formController.getForm("test", "väärä");
    }

}
