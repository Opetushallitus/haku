/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija;

import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ExceptionControllerTest {

    private ExceptionController exceptionController = new ExceptionController();

    @Test
    public void testResourceNotFoundExceptionsView() throws Exception {
        ModelAndView modelAndView = exceptionController.resourceNotFoundExceptions(new ResourceNotFoundException("viesti"));
        assertEquals(modelAndView.getViewName(), ExceptionController.ERROR_NOTFOUND);
    }

    @Test
    public void testResourceNotFoundExceptionsMessage() throws Exception {
        String actualErrorMessage = "viesti";
        ModelAndView modelAndView = exceptionController.resourceNotFoundExceptions(new ResourceNotFoundException(actualErrorMessage));
        Object expectedErrorMessage = modelAndView.getModel().get(ExceptionController.MODEL_MESSAGE);
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testResourceNotFoundExceptionsStackTrace() throws Exception {
        ModelAndView modelAndView = exceptionController.resourceNotFoundExceptions(new ResourceNotFoundException(""));
        Map<String, Object> model = modelAndView.getModel();
        assertTrue(model.containsKey(ExceptionController.MODEL_STACK_TRACE));
    }

    @Test
    public void testExceptions() throws Exception {
        ModelAndView modelAndView = exceptionController.exceptions(new ResourceNotFoundException("viesti"));
        assertEquals(modelAndView.getViewName(), ExceptionController.ERROR_SERVERERROR);
    }
}
