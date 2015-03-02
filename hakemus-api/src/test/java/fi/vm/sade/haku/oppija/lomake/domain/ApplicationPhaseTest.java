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

package fi.vm.sade.haku.oppija.lomake.domain;

import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApplicationPhaseTest {
    private final static String AS_ID = "AS_ID";
    private final static String ID = "ID";
    private final Map<String, String> answers = new HashMap<String, String>();
    private final ApplicationPhase applicationPhase = new ApplicationPhase(AS_ID, ID, answers);

    @Test
    public void testGetApplicationSystemId() throws Exception {
        assertEquals(AS_ID, applicationPhase.getApplicationSystemId());
    }

    @Test
    public void testGetPhaseId() throws Exception {
        assertEquals(ID, applicationPhase.getPhaseId());
    }

    @Test
    public void testGetVastaukset() throws Exception {
        assertEquals(answers, applicationPhase.getAnswers());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructNullApplicationSystemId() throws Exception {
        new ApplicationPhase(null, ID, answers);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructNullPhaseId() throws Exception {
        new ApplicationPhase(AS_ID, null, answers);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructNullAnswers() throws Exception {
        new ApplicationPhase(AS_ID, ID, null);
    }
}
