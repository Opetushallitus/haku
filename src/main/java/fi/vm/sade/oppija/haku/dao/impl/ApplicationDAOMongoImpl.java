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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakuLomakeId;
import fi.vm.sade.oppija.haku.domain.User;
import fi.vm.sade.oppija.haku.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl implements ApplicationDAO {

    public static final String HAKU_ID = "hakuId";
    public static final String LOMAKE_ID = "lomakeId";
    public static final String USER_ID = "userid";
    public static final String VAIHE_ID = "vaiheId";
    public static final String HAKEMUS_DATA = "hakemusData";
    public static final String HAKEMUS = "hakemus";
    private static final String SEQUENCE_FIELD = "seq";
    private static final String OID_PREFIX = "1.2.3.4.5.";

    @Override
    public HakemusState tallennaVaihe(HakemusState state) {
        DBObject query = new BasicDBObject();
        final HakuLomakeId hakuLomakeId = state.getHakemus().getHakuLomakeId();
        query.put(HAKU_ID, hakuLomakeId.getApplicationPeriodId());
        query.put(LOMAKE_ID, hakuLomakeId.getFormId());
        final String userName = state.getHakemus().getUser().getUserName();
        query.put(USER_ID, userName);
        DBObject one = getCollection().findOne(query);
        Map<String, Map<String, String>> vastaukset = new HashMap<String, Map<String, String>>();
        if (one != null) {
            vastaukset.putAll((Map<String, Map<String, String>>) one.toMap().get(HAKEMUS_DATA));
        } else {
            one = new BasicDBObject();
            one.put(HAKU_ID, hakuLomakeId.getApplicationPeriodId());
            one.put(LOMAKE_ID, hakuLomakeId.getFormId());
            one.put(Hakemus.HAKEMUS_OID, OID_PREFIX + getNextId());
            one.put(USER_ID, userName);
        }
        vastaukset.put(state.getVaiheId(), state.getHakemus().getVastaukset());
        one.put(HAKEMUS_DATA, vastaukset);
        one.put(VAIHE_ID, state.getVaiheId());
        getCollection().update(query, one, true, false);
        return new HakemusState(new Hakemus(hakuLomakeId, state.getHakemus().getUser(), vastaukset), state.getVaiheId());
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
        DBObject dbObject = new BasicDBObject();
        dbObject.put(HAKU_ID, hakuLomakeId.getApplicationPeriodId());
        dbObject.put(LOMAKE_ID, hakuLomakeId.getFormId());
        dbObject.put(USER_ID, user.getUserName());
        final DBObject one = getCollection().findOne(dbObject);
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        if (one != null) {
            map = (Map<String, Map<String, String>>) one.toMap().get(HAKEMUS_DATA);
        }

        return new Hakemus(hakuLomakeId, user, map);
    }

    @Override
    public List<Hakemus> findAll(User user) {
        List<Hakemus> list = new ArrayList<Hakemus>();
        DBObject dbObject = new BasicDBObject();
        dbObject.put(USER_ID, user.getUserName());
        final DBCursor dbObjects = getCollection().find(dbObject);
        for (DBObject object : dbObjects) {
            list.add(dbObjectToHakemus(object));
        }
        return list;
    }

    @Override
    public Hakemus find(String oid) {
        final BasicDBObject basicDBObject = new BasicDBObject();
        if (!oid.startsWith(OID_PREFIX)) {
            throw new RuntimeException("invalid oid");
        }
        basicDBObject.put(Hakemus.HAKEMUS_OID, oid);
        final DBObject one = getCollection().findOne(basicDBObject);
        if (one == null) {
            throw new ResourceNotFoundException("no hakemus found with oid " + oid);
        }
        return dbObjectToHakemus(one);
    }

    private Hakemus dbObjectToHakemus(final DBObject dbObject) {
        HakuLomakeId hakuLomakeId = new HakuLomakeId((String) dbObject.get(HAKU_ID), (String) dbObject.get(LOMAKE_ID));
        User user = new User((String) dbObject.get(USER_ID));
        Hakemus hakemus = new Hakemus(hakuLomakeId, user, (Map<String, Map<String, String>>) dbObject.toMap().get(HAKEMUS_DATA));
        hakemus.addMeta(Hakemus.HAKEMUS_OID, dbObject.get(Hakemus.HAKEMUS_OID).toString());
        return hakemus;
    }
}
