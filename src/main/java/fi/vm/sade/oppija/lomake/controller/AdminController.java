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

package fi.vm.sade.oppija.lomake.controller;

import fi.vm.sade.oppija.lomake.converter.FormModelToJsonString;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.elements.Attachment;
import fi.vm.sade.oppija.lomake.service.AdminService;
import fi.vm.sade.oppija.lomake.service.FormModelHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/12/1210:14 AM}
 * @since 1.1
 */
@Controller
@RequestMapping(value = "/admin")
@Secured("ROLE_ADMIN")
public class AdminController {

    public static final String ADMIN_UPLOAD_VIEW = "admin/upload";
    public static final String ATTACHMENT_MODEL = "attachment";
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

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getIndex() {
        ModelAndView modelAndView = new ModelAndView("admin/index");

        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put("mongodb.url", mongoUrl);
        properties.put("mongo.db.name", mongoDbName);
        properties.put("mongo.test-db.name", mongoTestDbName);
        properties.put("tarjonta.index.url", tarjontaIndexUrl);
        properties.put("tarjonta.data.url", tarjontaDataUrl);

        modelAndView.getModel().put("properties", properties);

        return modelAndView;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public ModelAndView upload() {
        return toUpload();
    }

    @RequestMapping(value = "/model", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public FormModel asJson() {
        return formModelHolder.getModel();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET, produces = "text/html; charset=UTF-8")
    public ModelAndView editModel() {
        final FormModel model = formModelHolder.getModel();
        final ModelAndView modelAndView = new ModelAndView("admin/editModel");
        final String convert = new FormModelToJsonString().convert(model);
        modelAndView.addObject("model", convert);
        return modelAndView;
    }

    @RequestMapping(value = "/edit/post", method = RequestMethod.POST, consumes = "multipart/form-data; charset=UTF-8")
    public String doActualEdit(HttpServletRequest request, @RequestParam("model") String json) {
        adminService.replaceModel(json);
        return "redirect:/admin";
    }


    private ModelAndView toUpload() {
        final ModelAndView modelAndView = new ModelAndView(ADMIN_UPLOAD_VIEW);
        modelAndView.addObject(ATTACHMENT_MODEL, new Attachment("file", "Lataa malli json-objektina"));
        return modelAndView;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView receiveFile(@RequestParam("file") MultipartFile file) throws IOException {

        adminService.replaceModel(file.getInputStream());
        return toUpload();
    }

}
