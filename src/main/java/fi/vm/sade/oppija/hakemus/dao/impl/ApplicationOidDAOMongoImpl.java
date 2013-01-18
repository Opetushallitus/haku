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

package fi.vm.sade.oppija.hakemus.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.hakemus.dao.ApplicationOidDAO;
import fi.vm.sade.oppija.lomake.dao.DBFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Mongodb implementation of ApplicationOidDAO
 * @author Mikko Majapuro
 */
@Service("applicationOidDAOMongoImpl")
public class ApplicationOidDAOMongoImpl implements ApplicationOidDAO {

    private static final String SEQUENCE_FIELD = "seq";
    private static final String SEQUENCE_NAME = "applicationsequence";

    @Autowired
    protected DBFactoryBean factoryBean;
    protected DB db;

    @Value("${application.oid.prefix}")
    private String oidPrefix;

    @PostConstruct
    protected void init() {
        this.db = factoryBean.getObject();
    }

    @Override
    public String generateNewOid() {
        DBCollection seq = getSequence();
        DBObject change = new BasicDBObject(SEQUENCE_FIELD, 1);
        DBObject update = new BasicDBObject("$inc", change); // the $inc here is a mongodb command for increment

        // Atomically updates the sequence field and returns the value for you
        final BasicDBObject query = new BasicDBObject();

        DBObject res;

        if (seq.getCount(query) == 0) {
            DBObject initialObject = new BasicDBObject();
            initialObject.put("seq", Integer.valueOf(0));
            seq.insert(initialObject);
            res = seq.findOne(query);
        } else {
            res = seq.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);
        }

        return oidPrefix + res.get(SEQUENCE_FIELD).toString();
    }

    protected DBCollection getSequence() {
        return db.getCollection(SEQUENCE_NAME);
    }
}
