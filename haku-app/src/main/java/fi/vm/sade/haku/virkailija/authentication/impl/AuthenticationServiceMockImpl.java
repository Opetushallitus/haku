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
package fi.vm.sade.haku.virkailija.authentication.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile(value = {"dev", "it"})
public class AuthenticationServiceMockImpl implements AuthenticationService {

    public static final int RANGE_SIZE = 1000000000;
    private static final String OID_PREFIX = "1.2.246.562.24.";

    public String addPerson(Person person) {
        return OID_PREFIX + String.format("%011d", Math.round(Math.random() * RANGE_SIZE));
    }

    @Override
    public List<String> getOrganisaatioHenkilo() {
        return Lists.newArrayList("1.2.246.562.10.84682192491", "1.2.246.562.10.00000000001", "1.2.246.562.10.94550468022");
    }

    @Override
    public String getStudentOid(String personOid) {
        return personOid;
    }

    @Override
    public String checkStudentOid(String personOid) {
        return personOid;
    }
}
