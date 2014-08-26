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

import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertTrue;

public class ResourceNotFoundExceptionMapperTest {

    public static final ResourceNotFoundException MESSAGE = new ResourceNotFoundException("message");
    private ResourceNotFoundExceptionMapper resourceNotFoundExceptionRuntimeMapper;

    @Before
    public void setUp() throws Exception {
        resourceNotFoundExceptionRuntimeMapper = new ResourceNotFoundExceptionMapper();
    }

    @Test
    public void testToResponseStatus() throws Exception {
        Response response = resourceNotFoundExceptionRuntimeMapper.toResponse(MESSAGE);
        assertTrue("Wrong http status code", response.getStatus() == Response.Status.NOT_FOUND.getStatusCode());
    }

}
