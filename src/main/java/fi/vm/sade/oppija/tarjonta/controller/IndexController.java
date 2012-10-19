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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.net.URL;

@Controller
public class IndexController {

    private static String xmlUrl = "file:/home/ville/src/oph/haku/trunk/src/main/resources/learningDownloadPOC.xml";

    final IndexService indexService;

    @Autowired
    public IndexController(final IndexService indexService) {
        this.indexService = indexService;
    }

    @RequestMapping(value = "/index/update")
    public
    @ResponseBody
    String addNotes() throws MalformedURLException {
        return Boolean.toString(indexService.update(new URL(xmlUrl)));
    }

}
