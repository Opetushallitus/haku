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

import static fi.vm.sade.haku.AuthenticationProviderForTesting.OFFICER_USER_OID;
import static fi.vm.sade.haku.AuthenticationProviderForTesting.OTHER_OFFICER_USER_OID;
import com.google.common.collect.Lists;

import fi.vm.sade.haku.AuthenticationProviderForTesting;
import fi.vm.sade.haku.virkailija.authentication.KayttooikeusService;
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
@Profile(value = {"dev", "it"})
public class KayttooikeusServiceMockImpl implements KayttooikeusService {

    public static final int RANGE_SIZE = 1000000000;
    private static final String OID_PREFIX = "1.2.246.562.24.";

    @Override
    public List<String> getOrganisaatioHenkilo() {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null
                || SecurityContextHolder.getContext().getAuthentication().getName() == null) {
            return Lists.newArrayList("1.2.246.562.10.84682192491", "1.2.246.562.10.00000000001", "1.2.246.562.10.94550468022");
        }
        String userOid = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userOid.equals(OFFICER_USER_OID)) {
            return Lists.newArrayList("1.2.246.562.10.84682192491", "1.2.246.562.10.00000000001", "1.2.246.562.10.94550468022");
        } else if (userOid.equals(OTHER_OFFICER_USER_OID)) {
            return Lists.newArrayList("1.2.246.562.10.61042218794");
        } else {
            return new ArrayList<>();
        }
    }

}
