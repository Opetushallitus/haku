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
import fi.vm.sade.oppija.common.authentication.AuthenticationService;
import fi.vm.sade.oppija.common.authentication.Person;
import fi.vm.sade.oppija.lomake.converter.FormModelToJsonString;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Attachment;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static javax.ws.rs.core.Response.created;


@Controller
@Path("/test")
public class TestController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private Environment env;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/addPerson")
    public String getIndex() {
        System.out.println("env.activeProfiles: "+ Arrays.asList(env.getActiveProfiles()));
        Person p = new Person("Testi", "Testi", "Persoona", null, true, "testi.persoona@oph.fi", "MALE", "Helsinki", false, "fi", "FINLAND", "fi");
        return "authenticationService: "+authenticationService+", result: "+authenticationService.addPerson(p);
    }

}
