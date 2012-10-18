package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.haku.dao.impl.ApplicationDAOMongoImpl;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.event.EventHandler;
import fi.vm.sade.oppija.haku.service.SessionDataHolder;
import fi.vm.sade.oppija.haku.service.UserHolder;
import fi.vm.sade.oppija.haku.service.impl.AdditionalQuestionServiceImpl;
import fi.vm.sade.oppija.haku.service.impl.HakemusServiceImpl;
import fi.vm.sade.oppija.haku.service.impl.UserDataStorage;
import org.junit.Before;
import org.junit.Test;
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
        final EventHandler eventHandler = new EventHandler();
        final FormModelDummyMemoryDaoImpl formService = new FormModelDummyMemoryDaoImpl(formId, firstCategoryId);
        final HakemusServiceImpl hakemusService = new HakemusServiceImpl(new UserDataStorage(new SessionDataHolder(), new ApplicationDAOMongoImpl(), new UserHolder()), eventHandler, formService);
        this.formController = new FormController(formService, new AdditionalQuestionServiceImpl(formService, hakemusService), hakemusService);
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
        ModelAndView actualModelAndView = formController.getCategory(applicationPeriodId, formId, firstCategoryId, null, null, null);
        assertEquals(firstCategoryId, ((Vaihe) actualModelAndView.getModel().get("category")).getId());
    }

    @Test
    public void testGetCategoryMVForm() throws Exception {
        ModelAndView actualModelAndView = formController.getCategory(applicationPeriodId, formId, firstCategoryId, null, null, null);
        assertEquals(formId, ((Form) actualModelAndView.getModel().get("form")).getId());
    }

    @Test
    public void testGetCategoryModelSize() throws Exception {
        ModelAndView actualModelAndView = formController.getCategory(applicationPeriodId, formId, firstCategoryId, null, null, null);
        assertEquals(5, actualModelAndView.getModel().size());
    }

    @Test
    public void testGetCategoryView() throws Exception {
        ModelAndView actualModelAndView = formController.getCategory(applicationPeriodId, formId, firstCategoryId, null, null, null);
        assertEquals(FormController.DEFAULT_VIEW, actualModelAndView.getViewName());
    }

    @Test
    public void testGetCategoryWrongView() throws Exception {
        ModelAndView actualModelAndView = formController.getCategory(applicationPeriodId, formId, firstCategoryId, null, null, null);
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
