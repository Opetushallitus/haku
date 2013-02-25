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

package fi.vm.sade.oppija.lomake.dao;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author jukka
 * @version 8/31/123:23 PM}
 * @since 1.1
 */
public class DBFactoryBean implements FactoryBean<DB> {
    private static final Logger LOG = LoggerFactory.getLogger(DBFactoryBean.class);
    protected Mongo mongo;
    private String name;

    @PostConstruct
    public void init() {
        LOG.debug("Using db " + mongo.debugString());
        mongo.setWriteConcern(WriteConcern.SAFE);
    }

    @PreDestroy
    public void shutDown() {
        mongo.close();
    }

    @Override
    public DB getObject() {
        return mongo.getDB(name);
    }

    @Override
    public Class<?> getObjectType() {
        return DB.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Required
    public void setMongo(Mongo mongo) {
        this.mongo = mongo;
    }

    @Required
    public void setName(String name) {
        this.name = name;
    }
}
