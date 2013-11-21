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

package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationOidDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * Mongodb implementation of ApplicationOidDAO
 *
 * @author Mikko Majapuro
 */
@Service("applicationOidDAOMongoImpl")
public class ApplicationOidDAOMongoImpl implements ApplicationOidDAO {

    private static final String SEQUENCE_FIELD = "seq";
    private static final String SEQUENCE_NAME = "applicationsequence";
    private final MongoTemplate mongoTemplate;
    private final String oidPrefix;

    @Autowired
    public ApplicationOidDAOMongoImpl(final MongoTemplate mongoTemplate,
                                      @Value("${application.oid.prefix}") final String oidPrefix) {
        this.mongoTemplate = mongoTemplate;
        this.oidPrefix = oidPrefix;
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
            initialObject.put("seq", Long.valueOf(0));
            seq.insert(initialObject);
            res = seq.findOne(query);
        } else {
            res = seq.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);
        }

        return oidPrefix + "." + formatOid(res.get(SEQUENCE_FIELD).toString());
    }

    @Override
    public String getOidPrefix() {
        return oidPrefix;
    }

    protected DBCollection getSequence() {
        return mongoTemplate.getCollection(SEQUENCE_NAME);
    }

    String formatOid(String oid) {
        oid = oid + checksum(oid);
        return String.format("%011d", Integer.valueOf(oid));
    }

    String checksum(String oid) {
        int sum = 0;
        int[] multipliers = new int[]{7, 3, 1}; //NOSONAR
        int multiplierIndex = 0;
        for (int i = oid.length() - 1; i >= 0; i--) {
            int curr = Integer.valueOf(String.valueOf(oid.charAt(i)));
            sum += curr * multipliers[multiplierIndex % 3]; //NOSONAR
            multiplierIndex++;
        }
        sum = (10 - (sum % 10)) % 10; // Summa vähennetään seuraavasta tasakymmenestä //NOSONAR
        return String.valueOf(sum);
    }
}
