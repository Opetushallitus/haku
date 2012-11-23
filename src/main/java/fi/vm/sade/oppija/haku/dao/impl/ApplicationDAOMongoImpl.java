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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.converter.DBObjectToHakemusConverter;
import fi.vm.sade.oppija.haku.converter.HakemusToBasicDBObjectConverter;
import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakuLomakeId;
import fi.vm.sade.oppija.haku.domain.User;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl implements ApplicationDAO {

    public static final String HAKEMUS = "hakemus";
    private static final String SEQUENCE_FIELD = "seq";
    private static final String OID_PREFIX = "1.2.3.4.5.";

    @Override
    public HakemusState tallennaVaihe(HakemusState state) {
        final Hakemus hakemus = state.getHakemus();
        final BasicDBObject query = new HakemusToBasicDBObjectConverter().convert(state.getHakemus());

        DBObject one = getCollection().findOne(query);
        if (one == null) {
            hakemus.addMeta(Hakemus.HAKEMUS_OID, OID_PREFIX + getNextId());
            one = new HakemusToBasicDBObjectConverter().convert(hakemus);
        }
        getCollection().update(query, one, true, false);
        return state;
    }

    public String getNextId() {

        DBCollection seq = getSequence();
        DBObject change = new BasicDBObject(SEQUENCE_FIELD, 1);
        DBObject update = new BasicDBObject("$inc", change); // the $inc here is a mongodb command for increment

        // Atomically updates the sequence field and returns the value for you
        final BasicDBObject query = new BasicDBObject("$eq", change);
        //final BasicDBObject query = new BasicDBObject();
        DBObject res = seq.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);
        return res.get(SEQUENCE_FIELD).toString();
    }

    @Override
    protected String getCollectionName() {
        return HAKEMUS;
    }

    @Override
    public Hakemus find(HakuLomakeId hakuLomakeId, User user) {

        Hakemus hakemus = new Hakemus(hakuLomakeId, user);
        final BasicDBObject convert = new HakemusToBasicDBObjectConverter().convert(hakemus);
        final DBObject one = getCollection().findOne(convert);
        if (one != null) {
            hakemus = dbObjectToHakemus(one);
        }
        return hakemus;
    }

    @Override
    public List<Hakemus> findAll(User user) {
        List<Hakemus> list = new ArrayList<Hakemus>();

        final ObjectMapper objectMapper = new ObjectMapper();
        final Map map = objectMapper.convertValue(user, Map.class);

        DBObject dbObject = new BasicDBObject("user", map);
        final DBCursor dbObjects = getCollection().find(dbObject);
        for (DBObject object : dbObjects) {
            list.add(dbObjectToHakemus(object));
        }
        return list;
    }

    @Override
    public HakemusState laitaVireille(HakemusState state) {
        final String oid = state.getHakemus().getMeta().get(Hakemus.HAKEMUS_OID);

        final DBObject update = findByOid(searchByOid(oid));
        update.put(Hakemus.STATEKEY, Hakemus.State.VIREILLÄ.toString());
        getCollection().findAndModify(searchByOid(oid), update);
        return state;
    }

    @Override
    public Hakemus find(String oid) {
        final DBObject one = findByOid(searchByOid(oid));
        if (one == null) {
            throw new ResourceNotFoundException("no hakemus found with oid " + oid);
        }
        return dbObjectToHakemus(one);
    }

    private DBObject findByOid(BasicDBObject basicDBObject1) {
        return getCollection().findOne(basicDBObject1);
    }

    private BasicDBObject searchByOid(String oid) {
        if (!oid.startsWith(OID_PREFIX)) {
            throw new RuntimeException("invalid oid");
        }
        final BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put(Hakemus.HAKEMUS_OID, oid);
        return basicDBObject;
    }

    private Hakemus dbObjectToHakemus(final DBObject dbObject) {
        return new DBObjectToHakemusConverter().convert(dbObject);
    }
}
