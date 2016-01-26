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

package fi.vm.sade.haku.provider;

import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Provider
@Component
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceNotFoundExceptionMapper.class);
    public static final String ERROR_PAGE = "/error/error";
    public static final String MODEL_STACK_TRACE = "stackTrace";
    public static final String MODEL_MESSAGE = "message";
    public static final String ERROR_ID = "error_id";
    public static final String ERROR_TIMESTAMP = "timestamp";

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        String timestamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
        String uuid = UUID.randomUUID().toString();
        LOGGER.warn("Error (" + uuid + "): " + exception.getMessage());
        Map<String, String> model = ImmutableMap.of(
                MODEL_STACK_TRACE, exception.toString(),
                MODEL_MESSAGE, exception.getMessage(),
                ERROR_ID, uuid,
                ERROR_TIMESTAMP, timestamp);
        return Response.status(Response.Status.NOT_FOUND).entity(new Viewable(ERROR_PAGE, model)).build();
    }
}
