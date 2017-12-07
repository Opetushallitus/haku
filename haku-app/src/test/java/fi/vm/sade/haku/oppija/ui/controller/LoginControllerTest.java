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

package fi.vm.sade.haku.oppija.ui.controller;

import org.glassfish.jersey.server.mvc.Viewable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    public static final String LOCATION_HEADER_NAME = "Location";
    private LoginController loginController = new LoginController();

    private HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    private SecurityContext securityContext = mock(SecurityContext.class);
    private Principal principal = mock(Principal.class);
    private HttpSession httpSession = mock(HttpSession.class);

    @Before
    public void setUp() throws Exception {
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        when(httpServletRequest.getContextPath()).thenReturn("");
        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("admin");
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock aInvocation) throws Throwable {
                return null;
            }
        }).when(httpSession).setAttribute(anyString(), anyObject());
    }

    @Test
    public void testLogin() throws Exception {
        Viewable viewable = loginController.login();
        assertEquals(LoginController.TOP_LOGIN_VIEW, viewable.getTemplateName());
    }

    @Test
    public void testLogout() throws Exception {
        Viewable viewable = loginController.logout();
        assertEquals(LoginController.LOGIN_VIEW, viewable.getTemplateName());
    }

    @Test
    public void testPostLoginRedirectStatus() throws Exception {
        Response response = loginController.postLoginRedirect(httpServletRequest, securityContext);
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
    }

    @Test
    public void testPostLoginRedirectLocation() throws Exception {
        testLocation("admin", "/");
    }

    @Test
    public void testPostLoginRedirectLocationOfficer() throws Exception {
        testLocation("officer", "virkailija/hakemus");
    }

    @Test
    public void testPostLoginRedirectLocationOma() throws Exception {
        testLocation("test", "/");
    }

    private void testLocation(final String name, final String expectedLocation) throws URISyntaxException {
        when(principal.getName()).thenReturn(name);
        Response response = loginController.postLoginRedirect(httpServletRequest, securityContext);
        assertEquals(new URI(expectedLocation), response.getMetadata().get(LOCATION_HEADER_NAME).get(0));
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
    }
}
