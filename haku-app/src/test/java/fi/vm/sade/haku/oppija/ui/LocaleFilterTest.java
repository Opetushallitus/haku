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

package fi.vm.sade.haku.oppija.ui;


import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import fi.vm.sade.haku.virkailija.authentication.PersonBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocaleFilterTest {

    private HttpServletRequest httpServletRequest;
    private HttpSession session;
    private ContainerRequestContext context;
    private LocaleFilter localeFilter;
    private AuthenticationService authenticationService;

    private Person langFi;
    private Person langSv;
    private UriInfo mockUriInfo;

    @Before
    public void setUp() throws Exception {
        this.httpServletRequest = mock(HttpServletRequest.class);
        this.session = mock(HttpSession.class);
        this.context = mock(ContainerRequestContext.class);
        this.authenticationService = mock(AuthenticationService.class);

        Map<String, Cookie> cookieMap = new HashMap<>();

        langFi = PersonBuilder.start().setContactLanguage("fi").get();
        langSv = PersonBuilder.start().setContactLanguage("sv").get();

        when(httpServletRequest.getSession()).thenReturn(session);
        when(context.getCookies()).thenReturn(cookieMap);
        context.setRequestUri(new URI(""));
        localeFilter = new LocaleFilter(httpServletRequest);
        localeFilter.setAuthenticationService(authenticationService);
    }

    @Test
    public void testFilterFiParam() throws Exception {
        putLangParameter("fi");
        assertEquals("Container request changed", this.context, localeFilter.filterAndReturn(context));
    }

    @Test
    public void testPersonFi() throws Exception {
        when(authenticationService.getCurrentHenkilo()).thenReturn(langFi);
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        this.mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getQueryParameters()).thenReturn(queryParameters);
        when(context.getUriInfo()).thenReturn(mockUriInfo);

        ContainerRequestContext realContext = localeFilter.filterAndReturn(context);
        assertEquals("Container request changed", this.context, realContext);
    }

    private void putLangParameter(final String lang) {
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();
        queryParameters.putSingle(LocaleFilter.LANGUAGE_QUERY_PARAMETER_KEY, lang);
        this.mockUriInfo = mock(UriInfo.class);
        when(mockUriInfo.getQueryParameters()).thenReturn(queryParameters);
        when(context.getUriInfo()).thenReturn(mockUriInfo);
    }
}
