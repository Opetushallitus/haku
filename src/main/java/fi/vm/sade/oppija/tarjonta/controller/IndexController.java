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

package fi.vm.sade.oppija.tarjonta.controller;

import fi.vm.sade.oppija.tarjonta.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URL;

@Controller
@Secured("ROLE_ADMIN")
public class IndexController {

    @Autowired
    IndexService indexService;

    @RequestMapping(value = "/admin/index/update")
    public
    @ResponseBody
    String updateIndex() throws IOException {
        URL url = new ClassPathResource("learningDownloadPOC.xml").getURL();
        return Boolean.toString(indexService.update(url));
    }

    @RequestMapping(value = "/admin/index/generate")
    public
    @ResponseBody
    String generateIndex() throws IOException {
        return Boolean.toString(indexService.generate());
    }

    @RequestMapping(value = "/admin/index/drop")
    public
    @ResponseBody
    String dropIndex() throws IOException {
        return Boolean.toString(indexService.drop());
    }

}
