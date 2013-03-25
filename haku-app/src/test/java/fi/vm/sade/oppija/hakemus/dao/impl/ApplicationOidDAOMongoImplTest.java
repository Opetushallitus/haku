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

package fi.vm.sade.oppija.hakemus.dao.impl;

import fi.vm.sade.oppija.common.dao.AbstractDAOTest;
import fi.vm.sade.oppija.hakemus.dao.ApplicationOidDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author Mikko Majapuro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@ActiveProfiles(profiles = "dev")
public class ApplicationOidDAOMongoImplTest extends AbstractDAOTest {

    @Autowired
    @Qualifier("applicationOidDAOMongoImpl")
    private ApplicationOidDAO applicationOidDAO;

    @Override
    protected String getCollectionName() {
        return "application";
    }

    @Test
    public void testSequence() throws Exception {
        String oid1 = applicationOidDAO.generateNewOid();
        String oid2 = applicationOidDAO.generateNewOid();
        assertNotNull(oid1);
        assertNotNull(oid2);
        assertNotSame(oid1, oid2);
    }

    @Test
    public void testFormat() {
        // OIDin tarkistesumma lasketaan kuten suomalaisissa pankkiviitteissä.
        // Testiä varten summat laskettu Tuataralla, 
        // http://tarkistusmerkit.teppovuori.fi/tuatara.htm#viite
        ApplicationOidDAOMongoImpl mongoImpl = new ApplicationOidDAOMongoImpl();
        String formattedOid = mongoImpl.formatOid("2847535");
        assertEquals("00028475358", formattedOid);
        formattedOid = mongoImpl.formatOid("9837593");
        assertEquals("00098375938", formattedOid);
        formattedOid = mongoImpl.formatOid("2845834");
        assertEquals("00028458346", formattedOid);
        formattedOid = mongoImpl.formatOid("100000");
        assertEquals("00001000009", formattedOid);
        formattedOid = mongoImpl.formatOid("110000");
        assertEquals("00001100006", formattedOid);
        formattedOid = mongoImpl.formatOid("101000");
        assertEquals("00001010002", formattedOid);
        formattedOid = mongoImpl.formatOid("100100");
        assertEquals("00001001008", formattedOid);
        formattedOid = mongoImpl.formatOid("100010");
        assertEquals("00001000106", formattedOid);
        formattedOid = mongoImpl.formatOid("100001");
        assertEquals("00001000012", formattedOid);
    }
}
