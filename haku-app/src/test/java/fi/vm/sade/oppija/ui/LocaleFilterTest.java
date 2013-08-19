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

package fi.vm.sade.oppija.ui;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MultivaluedMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocaleFilterTest {

    private HttpServletRequest httpServletRequest;
    private HttpSession session;
    private ContainerRequest containerRequest;
    private LocaleFilter localeFilter;

    @Before
    public void setUp() throws Exception {
        this.httpServletRequest = mock(HttpServletRequest.class);
        this.session = mock(HttpSession.class);
        this.containerRequest = mock(ContainerRequest.class);
        when(httpServletRequest.getSession()).thenReturn(session);
        localeFilter = new LocaleFilter(httpServletRequest);

    }

    @Test
    public void testFilterFi() throws Exception {
        putLangParameter("fi");
        assertEquals("Container request changed", this.containerRequest, localeFilter.filter(containerRequest));
    }

    private void putLangParameter(final String lang) {
        MultivaluedMap<String, String> queryParameters = new MultivaluedMapImpl();
        queryParameters.putSingle(LocaleFilter.LANGUAGE_QUERY_PARAMETER_KEY, lang);
        when(containerRequest.getQueryParameters()).thenReturn(queryParameters);
    }
}
