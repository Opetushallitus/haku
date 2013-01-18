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

package fi.vm.sade.oppija.hakemus.dao;

import fi.vm.sade.oppija.common.dao.AbstractDAOTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * @author Mikko Majapuro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
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
}
