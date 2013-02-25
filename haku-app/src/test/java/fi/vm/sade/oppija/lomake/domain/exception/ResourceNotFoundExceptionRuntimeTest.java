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

package fi.vm.sade.oppija.lomake.domain.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResourceNotFoundExceptionRuntimeTest {

    public static final String MESSAGE = "virhe";
    public static final NullPointerException EXCEPTION = new NullPointerException();

    @Test
    public void testMessage() throws Exception {
        ResourceNotFoundExceptionRuntime resourceNotFoundExceptionRuntime = new ResourceNotFoundExceptionRuntime(MESSAGE);
        assertEquals(MESSAGE, resourceNotFoundExceptionRuntime.getMessage());
    }

    @Test
    public void testException() throws Exception {
        ResourceNotFoundExceptionRuntime resourceNotFoundExceptionRuntime = new ResourceNotFoundExceptionRuntime(MESSAGE, EXCEPTION);
        assertEquals(EXCEPTION, resourceNotFoundExceptionRuntime.getCause());
    }
}
