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
import com.sun.jersey.multipart.FormDataParam;
import fi.vm.sade.oppija.lomake.converter.FormModelToJsonString;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Attachment;
import fi.vm.sade.oppija.lomake.service.AdminService;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NText;
import static javax.ws.rs.core.Response.seeOther;


@Controller
@Path("/admin")
@Secured("ROLE_ADMIN")
public class AdminController {

    public static final String ADMIN_UPLOAD_VIEW = "/admin/upload";
    public static final String ADMIN_INDEX_VIEW = "/admin/index";
    public static final String ADMIN_EDIT_VIEW = "/admin/editModel";
    public static final Attachment ATTACHMENT_MODEL = new Attachment("file", createI18NText("Lataa malli json-objektina"));
    @Autowired
    AdminService adminService;

    @Autowired
    FormModelHolder formModelHolder;

    @Value("${mongodb.url}")
    private String mongoUrl;
    @Value("${mongo.db.name}")
    private String mongoDbName;
    @Value("${mongo.test-db.name}")
    private String mongoTestDbName;
    @Value("${tarjonta.index.url}")
    private String tarjontaIndexUrl;
    @Value("${tarjonta.data.url}")
    private String tarjontaDataUrl;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getIndex() {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put("mongodb.url", mongoUrl);
        properties.put("mongo.db.name", mongoDbName);
        properties.put("mongo.test-db.name", mongoTestDbName);
        properties.put("tarjonta.index.url", tarjontaIndexUrl);
        properties.put("tarjonta.data.url", tarjontaDataUrl);
        return new Viewable(ADMIN_INDEX_VIEW, properties);
    }

    @GET
    @Path("/upload")
    @Produces(MediaType.TEXT_HTML)
    public Viewable upload() {
        return new Viewable(ADMIN_UPLOAD_VIEW, ATTACHMENT_MODEL);
    }

    @GET
    @Path("/model")
    @Produces(MediaType.APPLICATION_JSON)
    public FormModel asJson() {
        return formModelHolder.getModel();
    }

    @GET
    @Path("/edit")
    @Produces(MediaType.TEXT_HTML)
    public Viewable editModel() {
        final String convert = new FormModelToJsonString().apply(formModelHolder.getModel());
        return new Viewable(ADMIN_EDIT_VIEW, convert);
    }

    @POST
    @Path("/edit/post")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response doActualEdit(@FormParam("model") String json) throws URISyntaxException {
        adminService.replaceModel(json);
        return seeOther(new URI("/admin")).build();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public Viewable receiveFile(@FormDataParam("file") InputStream inputStream) throws IOException {
        adminService.replaceModel(inputStream);
        return new Viewable(ADMIN_UPLOAD_VIEW, ATTACHMENT_MODEL);
    }

}
