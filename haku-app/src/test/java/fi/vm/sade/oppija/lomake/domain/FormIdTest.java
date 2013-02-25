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

package fi.vm.sade.oppija.lomake.domain;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author jukka
 * @version 10/8/129:52 AM}
 * @since 1.1
 */
public class FormIdTest {

    private static final String HAKU_ID = "hid";
    private static final String LOMAKE_ID = "lid";
    public static final FormId FORM_ID_1 = new FormId(HAKU_ID, LOMAKE_ID);
    public static final FormId FORM_ID_2 = new FormId(HAKU_ID, LOMAKE_ID);

    @Test
    public void testHakemusIdEquals() {
        assertEquals(FORM_ID_1, FORM_ID_2);
    }

    @Test
    public void testEqualsHashCode() {
        assertTrue(FORM_ID_1.hashCode() == FORM_ID_2.hashCode());
    }

    @Test
    public void testNotEquals() {
        assertFalse(FORM_ID_1.equals(new FormId(HAKU_ID, LOMAKE_ID + "skldjfs")));
    }

    @Test(expected = NullPointerException.class)
    public void testNullHakuId() {
        new FormId(null, LOMAKE_ID);
    }

    @Test(expected = NullPointerException.class)
    public void testNullLomakeId() {
        new FormId(HAKU_ID, null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullHakuIdAndLomakeId() {
        new FormId(null, null);
    }

    @Test
    public void testEqualsClass() {
        assertFalse(FORM_ID_1.equals(new Object()));
    }

}
