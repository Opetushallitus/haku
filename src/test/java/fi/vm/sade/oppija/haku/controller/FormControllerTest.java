package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.dao.impl.ApplicationDAOMongoImpl;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.service.SessionDataHolder;
import fi.vm.sade.oppija.haku.service.impl.HakemusServiceImpl;
import fi.vm.sade.oppija.haku.validation.FormValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;


public class FormControllerTest {

    private final String applicationPeriodId = "test";
    private final String formId = "yhteishaku";
    private final String firstCategoryId = "henkilotiedot";
    private FormController formController;


    @Before
    public void setUp() throws Exception {
        final FormModelDummyMemoryDaoImpl formService = new FormModelDummyMemoryDaoImpl();
        this.formController = new FormController(new FormModelDummyMemoryDaoImpl(formId, firstCategoryId), new HakemusServiceImpl(new SessionDataHolder(), new ApplicationDAOMongoImpl(), new FormValidator(formService)));
    }

    @Test
    public void testGetFormAndRedirectToFirstCategory() throws Exception {
        String actual = formController.getFormAndRedirectToFirstCategory("test", formId);
        String expected = "redirect:" + formId + "/" + firstCategoryId;
        assertEquals(expected, actual);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFormAndRedirectToFirstCategoryNotFound() throws Exception {
        formController.getFormAndRedirectToFirstCategory(applicationPeriodId, "väärä");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFormAndRedirectToFirstCategoryNullFromId() throws Exception {
        formController.getFormAndRedirectToFirstCategory(applicationPeriodId, null);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFormAndRedirectToFirstCategoryNullApplicationId() throws Exception {
        formController.getFormAndRedirectToFirstCategory(null, formId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetFormAndRedirectToFirstCategoryNullApplicationIdAndFormId() throws Exception {
        formController.getFormAndRedirectToFirstCategory(null, null);
    }

    @Test
    public void testGetCategoryMVCategory() throws Exception {
        ModelAndView actualModelAndView = formController.getCategory(applicationPeriodId, formId, firstCategoryId, new MockHttpSession());
        assertEquals(firstCategoryId, ((Category) actualModelAndView.getModel().get("category")).getId());
    }

    @Test
    public void testGetCategoryMVForm() throws Exception {
        ModelAndView actualModelAndView = formController.getCategory(applicationPeriodId, formId, firstCategoryId, new MockHttpSession());
        assertEquals(formId, ((Form) actualModelAndView.getModel().get("form")).getId());
    }

    @Test
    public void testGetCategoryModelSize() throws Exception {
        ModelAndView actualModelAndView = formController.getCategory(applicationPeriodId, formId, firstCategoryId, new MockHttpSession());
        assertEquals(3, actualModelAndView.getModel().size());
    }

    @Test
    public void testGetCategoryView() throws Exception {
        ModelAndView actualModelAndView = formController.getCategory(applicationPeriodId, formId, firstCategoryId, new MockHttpSession());
        assertEquals(FormController.DEFAULT_VIEW, actualModelAndView.getViewName());
    }

    @Test
    public void testGetCategoryWrongView() throws Exception {
        ModelAndView actualModelAndView = formController.getCategory(applicationPeriodId, formId, firstCategoryId, new MockHttpSession());
        assertNotSame(null, actualModelAndView.getViewName());
    }

    @Test
    public void testResourceNotFoundExceptionMessage() throws Exception {
        String message = "text";
        ResourceNotFoundException rnfv = new ResourceNotFoundException(message);
        ModelAndView modelAndView = formController.resourceNotFoundExceptions(rnfv);
        assertEquals(message, modelAndView.getModel().get("message"));
    }

    @Test
    public void testResourceNotFoundExceptionView() throws Exception {
        String message = "text";
        ResourceNotFoundException rnfv = new ResourceNotFoundException(message);
        ModelAndView modelAndView = formController.resourceNotFoundExceptions(rnfv);
        assertEquals(FormController.ERROR_NOTFOUND, modelAndView.getViewName());
    }

    @Test
    public void testExceptions() throws Exception {
        String message = "text";
        NullPointerException nullPointerException = new NullPointerException(message);
        ModelAndView modelAndView = formController.exceptions(nullPointerException);
        assertEquals(FormController.ERROR_SERVERERROR, modelAndView.getViewName());
    }
}
