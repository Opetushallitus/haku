/*
 *
 *  * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *  *
 *  * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 *  * soon as they will be approved by the European Commission - subsequent versions
 *  * of the EUPL (the "Licence");
 *  *
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * European Union Public Licence for more details.
 *
 */

package fi.vm.sade.oppija.hakemus.service.impl;

import fi.vm.sade.oppija.hakemus.dao.ApplicationOidDAO;
import fi.vm.sade.oppija.hakemus.service.ApplicationOidService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOidServiceImplTest {

    private ApplicationOidService applicationOidService;
    private ApplicationOidDAO dao;
    private static final String OID = "1.2.3.4.5.0";
    private static final String OID2 = "1.2.3.4.5.1";

    @Before
    public void setUp() {
        dao = mock(ApplicationOidDAO.class);
        when(dao.generateNewOid()).thenReturn(OID).thenReturn(OID2);
        applicationOidService = new ApplicationOidServiceImpl(dao);
    }

    @Test
    public void testGenerateNewOid() {
        String oid = applicationOidService.generateNewOid();
        assertNotNull(oid);
        assertEquals(OID, oid);
        oid = applicationOidService.generateNewOid();
        assertNotNull(oid);
        assertEquals(OID2, oid);
        verify(dao, times(2)).generateNewOid();
    }
}
