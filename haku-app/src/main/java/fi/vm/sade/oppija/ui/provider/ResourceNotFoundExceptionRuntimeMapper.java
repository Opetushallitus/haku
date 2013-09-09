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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.ui.provider;

import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.oppija.lomake.exception.ResourceNotFoundExceptionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class ResourceNotFoundExceptionRuntimeMapper implements ExceptionMapper<ResourceNotFoundExceptionRuntime> {
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceNotFoundExceptionRuntimeMapper.class);
    public static final String ERROR_NOTFOUND = "/error/notfound";
    public static final String MODEL_STACK_TRACE = "stackTrace";
    public static final String MODEL_MESSAGE = "message";

    @Override
    public Response toResponse(ResourceNotFoundExceptionRuntime exception) {
        LOGGER.error("Resource not found: ", exception);
        ImmutableMap<String, String> model = ImmutableMap.of(
                MODEL_STACK_TRACE, exception.toString(),
                MODEL_MESSAGE, exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND).entity(new Viewable(ERROR_NOTFOUND, model)).build();
    }
}
