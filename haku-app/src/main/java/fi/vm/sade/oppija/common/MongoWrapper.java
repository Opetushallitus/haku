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

package fi.vm.sade.oppija.common;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;


@Service
public class MongoWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(MongoWrapper.class);
    private final Mongo mongo;
    private final DB db;

    @Autowired
    public MongoWrapper(final Mongo mongo, @Value("${mongo.db.name}") String name) {
        LOG.debug("Construct db {} : {}", name, mongo.debugString());
        this.mongo = mongo;
        this.db = mongo.getDB(name);
        this.mongo.setWriteConcern(WriteConcern.SAFE);
    }

    @PreDestroy
    public void close() {
        LOG.debug("close mongo " + mongo.debugString());
        mongo.close();
    }

    public void dropDatabase() {
        LOG.debug("Drop database {}", mongo.debugString());
        db.dropDatabase();
    }

    public void dropCollection(final String name) {
        LOG.debug("Drop collection {} from database {}", name, mongo.debugString());
        db.getCollection(name).drop();
    }

    public DBCollection getCollection(final String name) {
        return this.db.getCollection(name);
    }
}
