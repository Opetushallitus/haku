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

package fi.vm.sade.oppija.common.dao;

import fi.vm.sade.oppija.lomake.dao.TestDBFactoryBean;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract base class for DAO tests.
 * Runs common initialization and tear down tasks like test data insertion
 * and deletion.
 *
 * @author hannu
 */
public abstract class AbstractDAOTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractDAOTest.class);
    @Autowired
    TestDBFactoryBean dbFactory;

    @After
    public void removeTestData() {
        try {
            dbFactory.getObject().getCollection(getCollectionName()).drop();
        } catch (Exception e) {
            LOGGER.error("Error removing test data", e);
        }

    }

    public TestDBFactoryBean getDbFactory() {
        return this.dbFactory;
    }


    protected abstract String getCollectionName();
}
