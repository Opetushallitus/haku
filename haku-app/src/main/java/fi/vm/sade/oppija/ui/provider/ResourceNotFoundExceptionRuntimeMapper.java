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

import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.oppija.lomake.exception.ResourceNotFoundExceptionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;

@Provider
@Component
public class ResourceNotFoundExceptionRuntimeMapper extends BaseExceptionMapper implements ExceptionMapper<ResourceNotFoundExceptionRuntime> {
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceNotFoundExceptionRuntimeMapper.class);

    @Override
    public Response toResponse(ResourceNotFoundExceptionRuntime exception) {
        Map<String, String> model = createModel(exception);
        return Response.status(Response.Status.NOT_FOUND).entity(new Viewable(ERROR_PAGE, model)).build();
    }
}
