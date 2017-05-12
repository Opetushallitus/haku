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
import fi.vm.sade.haku.virkailija.authentication.PersonBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile(value = {"dev", "it", "devluokka"})
public class AuthenticationServiceMockImpl implements AuthenticationService {

    public static final int RANGE_SIZE = 1000000000;
    private static final String OID_PREFIX = "1.2.246.562.24.";

    @Override
    public Person addPerson(Person person) {
        if(person == null) {
            return null;
        }
        PersonBuilder builder = PersonBuilder.start(person)
                .setPersonOid(OID_PREFIX + String.format("%011d", Math.round(Math.random() * RANGE_SIZE)));
        return builder.get();
    }

    @Override
    public Person getCurrentHenkilo() {
        return PersonBuilder.start()
                .setContactLanguage("fi")
                .setFirstNames("Etu Nimet")
                .setHomeCity("Kotikunta")
                .setLanguage("fi")
                .setLastName("Sukunimi")
                .setNationality("suomi")
                .setNickName("Etu")
                .setNoSocialSecurityNumber(false)
                .setPersonOid("1.2.246.562.24.00000000001")
                .setSecurityOrder(false)
                .setSex("MIES")
                .setSocialSecurityNumber("110794-354D")
                .setStudentOid("1.2.246.562.24.00000000001")
                .get();
    }

    @Override
    public Person getHenkilo(String personOid) {
        return getCurrentHenkilo();
    }

    @Override
    public String getLangCookieName() {
        return "testi18next";
    }

    @Override
    public List<Person> getHenkiloList(List<String> personOids) {
        return Arrays.asList(getCurrentHenkilo());
    }
}
