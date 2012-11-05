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

package fi.vm.sade.oppija.haku.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/oma")
public class NoteListController {

    public static final Logger LOGGER = LoggerFactory.getLogger(NoteListController.class);

    @RequestMapping(value = "/muistilista", method = RequestMethod.GET)
    public ModelAndView getUserNoteList() {
        LOGGER.debug("getUserNoteList");
        return new ModelAndView("tarjonta/notelist");
    }

    @RequestMapping(value = "/vertailu", method = RequestMethod.GET)
    public ModelAndView getUserComparison() {
        LOGGER.debug("getUserComparison");
        return new ModelAndView("tarjonta/comparison");
    }

}
