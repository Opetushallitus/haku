/*
 * Copyright (c) 2012. The Finnish Board of Education - Opetushallitus
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

import fi.vm.sade.oppija.haku.domain.PostOffice;
import fi.vm.sade.oppija.haku.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Mikko Majapuro
 */
@Controller
@RequestMapping(value = "/address")
public class AddressController {

    @Autowired
    AddressService addressService;

    @RequestMapping(value = "/{postalCode}/postoffice", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public PostOffice getPostOfficeByPostalCode(@PathVariable final String postalCode) {
        return addressService.findPostOfficeByPostalCode(postalCode);
    }
}
