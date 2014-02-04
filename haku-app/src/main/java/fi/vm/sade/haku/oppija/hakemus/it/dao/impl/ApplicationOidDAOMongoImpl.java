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
import fi.vm.sade.haku.oppija.lomake.exception.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class ApplicationOidDAOMongoImpl implements ApplicationOidDAO {

    public static final String SEQUENCE_FIELD = "seq";
    public static final String SEQUENCE_NAME = "applicationsequence";
    private final String oidPrefix;
    private final DBCollection sequenceCollection;
    private final DBObject update = new BasicDBObject("$inc", new BasicDBObject(SEQUENCE_FIELD, 1));
    public static final BasicDBObject EMPTY_QUERY = new BasicDBObject();

    @Autowired
    public ApplicationOidDAOMongoImpl(final MongoTemplate mongoTemplate,
                                      @Value("${application.oid.prefix}") final String oidPrefix) {
        this.oidPrefix = oidPrefix + ".";
        sequenceCollection = mongoTemplate.getCollection(SEQUENCE_NAME);
    }

    @Override
    public String generateNewOid() {
        String seq = sequenceCollection.findAndModify(EMPTY_QUERY,
                EMPTY_QUERY,
                EMPTY_QUERY,
                false, update, true, true).get(SEQUENCE_FIELD).toString();
        return oidPrefix + formatOid(seq);
    }

    public final String formatOid(final String oid) {
        return String.format("%011d", Integer.valueOf(oid + checksum(oid)));
    }

    private static final String checksum(final String oid) {
        int sum = 0;
        int[] multipliers = new int[]{7, 3, 1}; //NOSONAR
        int multiplierIndex = 0;
        for (int i = oid.length() - 1; i >= 0; i--) {
            int curr = Character.getNumericValue(oid.charAt(i));
            sum += curr * multipliers[multiplierIndex % 3]; //NOSONAR
            multiplierIndex++;
        }
        sum = (10 - (sum % 10)) % 10; // Summa vähennetään seuraavasta tasakymmenestä //NOSONAR
        return String.valueOf(sum);
    }
}
