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

package fi.vm.sade.haku.oppija.common.dao;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public abstract class AbstractDAOMongoImpl<T> implements BaseDAO<T> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    protected final Function<T, DBObject> toDBObject;
    protected final Function<DBObject, T> fromDBObject;

    public AbstractDAOMongoImpl(Function<DBObject, T> fromDBObject, Function<T, DBObject> toDBObject) {
        this.fromDBObject = fromDBObject;
        this.toDBObject = toDBObject;
    }

    protected abstract String getCollectionName();

    protected DBCollection getCollection() {
        return mongoTemplate.getCollection(getCollectionName());
    }

    @Override
    public List<T> find(T t) {
        final DBCursor dbCursor = getCollection().find(toDBObject.apply(t));
        return Lists.newArrayList(Iterables.transform(dbCursor, fromDBObject));
    }

    @Override
    public void save(T t) {
        getCollection().save(toDBObject.apply(t));
    }

    @Override
    public void update(T o, T n) {
        getCollection().update(toDBObject.apply(o), toDBObject.apply(n));
    }
}
