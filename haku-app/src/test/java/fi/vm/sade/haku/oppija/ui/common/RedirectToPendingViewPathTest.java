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

package fi.vm.sade.haku.oppija.ui.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RedirectToPendingViewPathTest extends ViewPathTest {

    public static final String OID = "oid";

    @Test
    public void testGetPath() throws Exception {
        RedirectToPendingViewPath redirectToPendingViewPath =
                new RedirectToPendingViewPath(APPLICATION_SYSTEM_ID, OID);
        assertEquals(REDIRECT_TO_FORM_PREFIX + "/valmis/" + OID, redirectToPendingViewPath.getPath());
    }

    @Test(expected = NullPointerException.class)
    public void testGetPathNullAsId() throws Exception {
        new RedirectToPendingViewPath(null, OID);
    }

    @Test(expected = NullPointerException.class)
    public void testGetPathNullOid() throws Exception {
        new RedirectToPendingViewPath(APPLICATION_SYSTEM_ID, null);
    }
}
