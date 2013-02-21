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

package fi.vm.sade.oppija.ui.controller;

/**
 * @author jukka
 * @version 10/12/121:20 PM}
 * @since 1.1
 */

import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.service.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import java.net.URISyntaxException;

import static javax.ws.rs.core.Response.seeOther;

@Component
@Path("user")
public class LoginController {

    public static final String TOP_LOGIN_VIEW = "/top/login";
    public static final String LOGIN_VIEW = "/index";
    public static final String USERNAME_SESSION_ATTRIBURE = "username";

    private final UserHolder userHolder;

    @Autowired
    public LoginController(final UserHolder userHolder) {
        this.userHolder = userHolder;
    }

    @GET
    @Path("postLogin")
    public Response postLoginRedirect(@Context HttpServletRequest req, @Context SecurityContext securityContext) throws URISyntaxException {

        String redirect = req.getParameter("redirect");
        if (redirect != null) {
            Response.temporaryRedirect(new URI(redirect));
        }

        String name = securityContext.getUserPrincipal().getName();
        userHolder.login(new User(name));

        req.getSession().setAttribute(USERNAME_SESSION_ATTRIBURE, name);

        return getResponseByUsername(name, req);
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

    private Response getResponseByUsername(String name, HttpServletRequest req) throws URISyntaxException {
        if (name.equals("admin") || name.equals("admin@oph.fi")) {
            return seeOther(new URI(req.getContextPath() + "/admin")).build();
        } else if (name.equals("officer")) {
            return seeOther(new URI(req.getContextPath() + "/virkailija/hakemus")).build();
        } else {
            return seeOther(new URI(req.getContextPath() + "/oma")).entity("").build();
        }
    }

}
