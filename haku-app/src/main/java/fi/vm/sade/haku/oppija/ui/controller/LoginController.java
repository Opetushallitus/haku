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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;

import static javax.ws.rs.core.Response.seeOther;

@Component
@Path("user")
@Profile(value = {"dev", "it", "devluokka"})
public class LoginController {

    public static final String TOP_LOGIN_VIEW = "/top/login";
    public static final String LOGIN_VIEW = "/index";

    @GET
    @Path("postLogin")
    public Response postLoginRedirect(@Context HttpServletRequest req, @Context SecurityContext securityContext) throws URISyntaxException {

        String redirect = req.getParameter("redirect");
        if (redirect != null) {
            Response.temporaryRedirect(new URI(redirect));
        }

        Principal userPrincipal = securityContext.getUserPrincipal();
        return getResponseByUsername(userPrincipal.getName());
    }

    @GET
    @Path("login")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Viewable login() {
        return new Viewable(TOP_LOGIN_VIEW);
    }

    @GET
    @Path("logout")
    @Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Viewable logout() {
        return new Viewable(LOGIN_VIEW);
    }

    private Response getResponseByUsername(String username) throws URISyntaxException {
        String path = "officer".equals(username) || "opo".equals(username) ? "virkailija/hakemus" : "/";
        return seeOther(new URI(path)).build();
    }
}
