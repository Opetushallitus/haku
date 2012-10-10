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

package fi.vm.sade.oppija.haku.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author jukka
 * @version 10/8/129:52 AM}
 * @since 1.1
 */
public class HakemusIdTest {

    private static final String ID = "foo_foo_foo_foo";

    @Test
    public void testHakemusId() {
        final String foo = "foo";
        final HakemusId hakemusId = new HakemusId(foo, foo, foo, foo);
        assertEquals(hakemusId.asKey(), ID);
    }

    @Test
    public void testHakemusIdFromString() {
        final HakemusId hakemusId = HakemusId.fromKey(ID);
        assertEquals(hakemusId.asKey(), ID);
    }

    @Test
    public void testEquals() {
        final HakemusId hakemusId = HakemusId.fromKey(ID);
        final HakemusId hakemusId2 = HakemusId.fromKey("foo_foo_foo_foo");
        assertEquals(hakemusId, hakemusId2);
    }

}
