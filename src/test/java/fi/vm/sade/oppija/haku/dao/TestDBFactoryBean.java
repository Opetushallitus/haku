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

package fi.vm.sade.oppija.haku.dao;

import fi.vm.sade.oppija.lomake.dao.DBFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;

public class TestDBFactoryBean extends DBFactoryBean {

    private static final Logger LOG = LoggerFactory.getLogger(TestDBFactoryBean.class);

    @PreDestroy
    public void drop() {
        try {
            LOG.debug("Drop database " + getObject().getName());
            getObject().dropDatabase();
        } catch (Exception e) {
            LOG.error("Could not drop test database.", e);
        }
    }
}
