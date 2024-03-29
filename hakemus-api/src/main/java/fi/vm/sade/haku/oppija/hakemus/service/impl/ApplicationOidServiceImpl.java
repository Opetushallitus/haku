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
package fi.vm.sade.haku.oppija.hakemus.service.impl;

import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationOidDAO;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationOidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationOidServiceImpl implements ApplicationOidService {

    private final ApplicationOidDAO applicationOidDAO;

    @Autowired
    public ApplicationOidServiceImpl(final ApplicationOidDAO applicationOidDAO) {
        this.applicationOidDAO = applicationOidDAO;
    }

    @Override
    public String generateNewOid() {
        return applicationOidDAO.generateNewOid();
    }
}
