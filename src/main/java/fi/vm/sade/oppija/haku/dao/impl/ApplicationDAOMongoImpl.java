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
        Hakemus queryHakemus = searchByLomakeIdAndUser(state);
        final BasicDBObject query = new HakemusToBasicDBObjectConverter().convert(queryHakemus);

        DBObject one = getCollection().findOne(query);
        if (one != null) {
            queryHakemus = new DBObjectToHakemusConverter().convert(one);
        }
        Hakemus uusiHakemus = state.getHakemus();
        Map<String, String> vastauksetMerged = uusiHakemus.getVastauksetMerged();
        queryHakemus.addVaiheenVastaukset(state.getVaiheId(), vastauksetMerged);
        queryHakemus.setVaiheId(uusiHakemus.getVaiheId());
        one = new HakemusToBasicDBObjectConverter().convert(queryHakemus);
        getCollection().update(query, one, true, false);
        return state;
    }

    public String getNextId() {

        DBCollection seq = getSequence();
        DBObject change = new BasicDBObject(SEQUENCE_FIELD, 1);
        DBObject update = new BasicDBObject("$inc", change); // the $inc here is a mongodb command for increment

        // Atomically updates the sequence field and returns the value for you
        //final BasicDBObject query = new BasicDBObject("$eq", change);
        final BasicDBObject query = new BasicDBObject();

        DBObject res = null;

        if (seq.getCount(query) == 0) {
            // running findAndModify with the upsert flag on results in a following error:
            // com.mongodb.CommandResult$CommandFailure: command failed [findandmodify]:
            // { "serverUsed" : "localhost/127.0.0.1:27017" , "errmsg" : "exception: upsert mode requires query field" , "code" : 13330 , "ok" : 0.0}
            DBObject initialObject = new BasicDBObject();
            initialObject.put("seq", Integer.valueOf(0));
            seq.insert(initialObject);
            res = seq.findOne(query);
        }
        else {
            res = seq.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);
        }

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
    public void laitaVireille(final HakuLomakeId hakulomakeId, final User user) {
        Hakemus hakemus = new Hakemus(hakulomakeId, user);
        final BasicDBObject query = new HakemusToBasicDBObjectConverter().convert(hakemus);
        DBObject update = new BasicDBObject("$set", new BasicDBObject(Hakemus.OID, OID_PREFIX + getNextId()));
        //update.put(Hakemus.VAIHE_ID, "valmis");
        getCollection().update(query, update);
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
        return new BasicDBObject("oid", oid);
    }

    private Hakemus dbObjectToHakemus(final DBObject dbObject) {
        return new DBObjectToHakemusConverter().convert(dbObject);
    }

    private Hakemus searchByLomakeIdAndUser(HakemusState state) {
        return new Hakemus(state.getHakemus().getHakuLomakeId(), state.getHakemus().getUser());
    }
}
