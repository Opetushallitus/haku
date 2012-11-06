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


import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

public class ExceptionController {

    public static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    public static final String ERROR_NOTFOUND = "error/notfound";
    public static final String ERROR_SERVERERROR = "error/servererror";
    public static final String MODEL_STACK_TRACE = "stackTrace";
    public static final String MODEL_MESSAGE = "message";

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView resourceNotFoundExceptions(ResourceNotFoundException e) {
        return createModelAndView(ERROR_NOTFOUND, e);
    }

    @ExceptionHandler(Throwable.class)
    public ModelAndView exceptions(Throwable t) {
        return createModelAndView(ERROR_SERVERERROR, t);
    }

    private ModelAndView createModelAndView(final String viewName, final Throwable t) {
        LOGGER.error("Sovellusvirhe:", t);
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject(MODEL_STACK_TRACE, t.toString());
        modelAndView.addObject(MODEL_MESSAGE, t.getMessage());
        return modelAndView;
    }

}
