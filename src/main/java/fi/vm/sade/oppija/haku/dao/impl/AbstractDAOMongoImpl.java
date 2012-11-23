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

package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import fi.vm.sade.oppija.haku.dao.DBFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class AbstractDAOMongoImpl {

    @Autowired
    protected DBFactoryBean factoryBean;
    protected DB db;

    @PostConstruct
    protected void init() {
        this.db = factoryBean.getObject();
    }

    protected DBCollection getCollection() {
        return db.getCollection(getCollectionName());
    }

    protected abstract String getCollectionName();

    protected DBCollection getSequence() {
        return db.getCollection(getSequenceName());
    }


    public String getSequenceName() {
        return getCollectionName() + "sequence";
    }
}
