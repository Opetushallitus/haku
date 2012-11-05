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

package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.domain.PostOffice;
import fi.vm.sade.oppija.haku.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mikko Majapuro
 */
@Service
public class AddressServiceImpl implements AddressService {

    private Map<String, PostOffice> postOffices;

    public AddressServiceImpl() {
        postOffices = new HashMap<String, PostOffice>();
        PostOffice helsinki = new PostOffice("Helsinki");
        PostOffice espoo = new PostOffice("Espoo");
        PostOffice tampere = new PostOffice("Tampere");
        postOffices.put("00180", helsinki);
        postOffices.put("00002", helsinki);
        postOffices.put("00100", helsinki);
        postOffices.put("00102", helsinki);
        postOffices.put("00120", helsinki);
        postOffices.put("00130", helsinki);
        postOffices.put("00140", helsinki);
        postOffices.put("00150", helsinki);
        postOffices.put("00160", helsinki);
        postOffices.put("00170", helsinki);
        postOffices.put("00190", helsinki);
        postOffices.put("00200", helsinki);
        postOffices.put("02100", espoo);
        postOffices.put("02110", espoo);
        postOffices.put("02120", espoo);
        postOffices.put("02130", espoo);
        postOffices.put("02140", espoo);
        postOffices.put("02150", espoo);
        postOffices.put("02160", espoo);
        postOffices.put("02170", espoo);
        postOffices.put("02230", espoo);
        postOffices.put("33100", tampere);
        postOffices.put("33310", tampere);
        postOffices.put("33540", tampere);
        postOffices.put("33200", tampere);
    }

    @Override
    public PostOffice findPostOfficeByPostalCode(String postalCode) {
        if (postalCode != null) {
            return postOffices.get(postalCode);
        }
        return null;
    }
}
