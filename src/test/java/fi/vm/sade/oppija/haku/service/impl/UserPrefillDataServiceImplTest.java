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

package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.service.UserHolder;
import fi.vm.sade.oppija.haku.service.UserPrefillDataService;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserPrefillDataServiceImplTest {

    public static final String KEY_AND_VALUE = "test";
    private UserPrefillDataService userPrefillDataService = new UserPrefillDataServiceImpl(new UserHolder());

    @Test
    public void testGetUserPrefillData() throws Exception {
        assertTrue(userPrefillDataService.getUserPrefillData().isEmpty());
    }

    @Test
    public void testGetUserPrefillDataAdd() throws Exception {
        HashMap<String, String> data = new HashMap<String, String>(1);
        data.put(KEY_AND_VALUE, KEY_AND_VALUE);
        userPrefillDataService.addUserPrefillData(data);
        assertEquals(1, userPrefillDataService.getUserPrefillData().size());
    }
}
