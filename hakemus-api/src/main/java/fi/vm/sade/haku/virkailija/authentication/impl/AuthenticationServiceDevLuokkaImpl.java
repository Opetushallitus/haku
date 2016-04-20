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

import fi.vm.sade.haku.oppija.configuration.UrlConfiguration;
import fi.vm.sade.haku.virkailija.authentication.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile(value = {"devluokka"})
public class AuthenticationServiceDevLuokkaImpl extends AuthenticationServiceImpl {

    @Autowired
    public AuthenticationServiceDevLuokkaImpl(
            UrlConfiguration urlConfiguration,
            @Value("${cas.service.authentication-service}") String targetService,
            @Value("${haku.app.username.to.usermanagement}") String clientAppUser,
            @Value("${haku.app.password.to.usermanagement}") String clientAppPass,
            @Value("${user.oid.prefix}") String userOidPrefix,
            @Value("${haku.langCookie}") String langCookieName) {
        super(urlConfiguration, targetService, clientAppUser, clientAppPass, userOidPrefix, langCookieName);
    }

    @Override
    public List<String> getOrganisaatioHenkilo() {
        return new AuthenticationServiceMockImpl().getOrganisaatioHenkilo();
    }

    @Override
    public Person getCurrentHenkilo() {
        return new AuthenticationServiceMockImpl().getCurrentHenkilo();
    }

    @Override
    public Person getHenkilo(String personOid) {
        return new AuthenticationServiceMockImpl().getHenkilo(personOid);
    }
}
