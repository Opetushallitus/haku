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

package fi.vm.sade.oppija.common.dao;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.*;
import fi.vm.sade.oppija.lomake.dao.DBFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

public abstract class AbstractDAOMongoImpl<T> implements BaseDAO<T> {

    private static final String SEQUENCE_FIELD = "seq";

    @Autowired
    protected DBFactoryBean factoryBean;
    protected DB db;

    protected final Function<T, DBObject> toDBObject;
    protected final Function<DBObject, T> fromDBObject;

    public AbstractDAOMongoImpl(Function<DBObject, T> fromDBObject, Function<T, DBObject> toDBObject) {
        this.fromDBObject = fromDBObject;
        this.toDBObject = toDBObject;
    }

    @PostConstruct
    protected void init() {
        this.db = factoryBean.getObject();
    }

    protected abstract String getCollectionName();

    protected DBCollection getCollection() {
        return db.getCollection(getCollectionName());
    }

    protected DBCollection getSequence() {
        return db.getCollection(getSequenceName());
    }

    public String getSequenceName() {
        return getCollectionName() + "sequence";
    }

    @Override
    public List<T> find(T t) {
        System.out.println(toDBObject.apply(t));
        final DBCursor dbCursor = getCollection().find(toDBObject.apply(t));
        return Lists.newArrayList(Iterables.transform(dbCursor, fromDBObject));
    }

    @Override
    public void delete(T t) {
        getCollection().remove(toDBObject.apply(t));
    }

    @Override
    public void update(T o, T n) {
        getCollection().update(toDBObject.apply(o), toDBObject.apply(n));
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
}
