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

package fi.vm.sade.oppija.hakemus.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.lomake.converter.DBObjectToHakemusConverter;
import fi.vm.sade.oppija.lomake.converter.HakemusToBasicDBObjectConverter;
import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.lomake.dao.impl.AbstractDAOMongoImpl;
import fi.vm.sade.oppija.lomake.domain.Application;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
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
    public ApplicationState tallennaVaihe(ApplicationState state) {
        Application queryApplication = searchByLomakeIdAndUser(state);
        final BasicDBObject query = new HakemusToBasicDBObjectConverter().convert(queryApplication);

        DBObject one = getCollection().findOne(query);
        if (one != null) {
            queryApplication = new DBObjectToHakemusConverter().convert(one);
        }
        Application uusiApplication = state.getHakemus();
        Map<String, String> vastauksetMerged = uusiApplication.getVastauksetMerged();
        queryApplication.addVaiheenVastaukset(state.getVaiheId(), vastauksetMerged);
        queryApplication.setVaiheId(uusiApplication.getVaiheId());
        one = new HakemusToBasicDBObjectConverter().convert(queryApplication);
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
        } else {
            res = seq.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);
        }

        return res.get(SEQUENCE_FIELD).toString();
    }

    @Override
    protected String getCollectionName() {
        return HAKEMUS;
    }

    @Override
    public Application find(FormId formId, User user) {

        Application application = new Application(formId, user);
        final BasicDBObject convert = new HakemusToBasicDBObjectConverter().convert(application);
        final DBObject one = getCollection().findOne(convert);
        if (one != null) {
            application = dbObjectToHakemus(one);
        }
        return application;
    }

    @Override
    public List<Application> findAll(User user) {
        List<Application> list = new ArrayList<Application>();

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
    public String laitaVireille(final FormId hakulomakeId, final User user) {
        Application application = new Application(hakulomakeId, user);
        final BasicDBObject query = new HakemusToBasicDBObjectConverter().convert(application);
        String oid = OID_PREFIX + getNextId();
        DBObject update = new BasicDBObject("$set", new BasicDBObject(Application.OID, oid));
        //update.put(Hakemus.VAIHE_ID, "valmis");
        getCollection().update(query, update);
        return oid;
    }

    @Override
    public Application find(String oid) {
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

    private Application dbObjectToHakemus(final DBObject dbObject) {
        return new DBObjectToHakemusConverter().convert(dbObject);
    }

    private Application searchByLomakeIdAndUser(ApplicationState state) {
        return new Application(state.getHakemus().getFormId(), state.getHakemus().getUser());
    }
}
