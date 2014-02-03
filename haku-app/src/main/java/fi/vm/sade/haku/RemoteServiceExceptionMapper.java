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

package fi.vm.sade.haku;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

@Provider
@Component
public class RemoteServiceExceptionMapper implements ExceptionMapper<RemoteServiceException> {
    public static final Logger LOGGER = LoggerFactory.getLogger(RemoteServiceExceptionMapper.class);

    @Override
    public Response toResponse(final RemoteServiceException exception) {
        String uuid = UUID.randomUUID().toString();
        LOGGER.error("Error: " + uuid, exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(uuid + ", " + exception.getMessage()).build();
    }
}
