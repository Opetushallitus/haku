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

import com.sun.jersey.api.view.Viewable;
import fi.vm.sade.oppija.hakemus.domain.ApplicationInfo;
import fi.vm.sade.oppija.hakemus.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;

@Controller
@Path("/oma")
@Secured("ROLE_USER")
public class PersonalServices {

    public static final Logger LOGGER = LoggerFactory.getLogger(PersonalServices.class);
    public static final String NOTELIST_VIEW = "/tarjonta/notelist";
    public static final String COMPARISON_VIEW = "/tarjonta/comparison";
    public static final String PERSONAL_TEMPLATE_VIEW = "/personal/template";
    public static final String USER_APPLICATION_INFO_MODEL = "UserApplicationInfo";
    public static final String SECTION_MODEL = "section";
    private static final String CHARSET_UTF_8 = ";charset=UTF-8";

    @Autowired
    public ApplicationService applicationService;

    @GET
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable hautKoulutuksiin() {
        return getApplications();
    }

    @GET
    @Path("vertailu")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getUserComparison() {
        return new Viewable(COMPARISON_VIEW);
    }

    @GET
    @Path("applications")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getApplications() {
        LOGGER.debug("getApplications");
        HashMap<String, Object> model = new HashMap<String, Object>(2);
        List<ApplicationInfo> userApplicationInfo = applicationService.getUserApplicationInfo();
        model.put(SECTION_MODEL, "applications");
        model.put(USER_APPLICATION_INFO_MODEL, userApplicationInfo);
        return new Viewable(PERSONAL_TEMPLATE_VIEW, model);
    }

    @GET
    @Path(value = "muistilista")
    @Produces(MediaType.TEXT_HTML + CHARSET_UTF_8)
    public Viewable getUserNoteList() {
        LOGGER.debug("getUserNoteList");
        return new Viewable(NOTELIST_VIEW);
    }

    @POST
    @Path(value = "muistilista")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void addApplicationOptionToNoteList(@FormParam("id") final String id) {
        LOGGER.debug("addApplicationOptionToNoteList " + id);
    }

    @POST
    @Path(value = "vertailu")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void addApplicationOptionToComparison(@FormParam("id") final String id) {
        LOGGER.debug("addApplicationOptionToComparison" + id);
    }

}
