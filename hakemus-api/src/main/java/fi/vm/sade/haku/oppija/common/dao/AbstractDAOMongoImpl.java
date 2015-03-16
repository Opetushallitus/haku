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
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public abstract class AbstractDAOMongoImpl<T> implements BaseDAO<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDAOMongoImpl.class);

    protected static final String OPTION_SPARSE = "sparse";
    protected static final String OPTION_NAME = "name";
    protected static final String OPTION_UNIQUE = "unique";
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

    public List<T> find(T t, int limit) {
        final DBCursor dbCursor = getCollection().find(toDBObject.apply(t)).limit(limit);
        return Lists.newArrayList(Iterables.transform(dbCursor, fromDBObject));
    }

    @Override
    public void save(T t) {
        getCollection().save(toDBObject.apply(t));
    }

    protected void ensureIndex(String name, String... fields){
        _ensureIndex(name, false, false, fields);
    }

    protected void ensureIndex(String name, Boolean isSparse, String... fields){
        _ensureIndex(name, false, isSparse, fields);
    }

    protected void ensureUniqueIndex(String name, String... fields){
        _ensureIndex(name, true, false, fields);
    }

    protected void ensureSparseIndex(String name, String... fields){
        _ensureIndex(name, false, true, fields);
    }

    private void _ensureIndex(String name, Boolean isUnique, Boolean isSparse, String... fields) {
        final DBObject options = new BasicDBObject(OPTION_NAME, name);
        options.put(OPTION_SPARSE, isSparse.booleanValue());
        if (isUnique){
            options.put(OPTION_UNIQUE, isUnique);
        }

        final DBObject index = new BasicDBObject();
        for (String field : fields) {
            index.put(field, 1);
        }
        LOGGER.info(this.getClass().getSimpleName() +": Executin ensure index " + index+ " with options " + options);
        getCollection().ensureIndex(index, options);
    }

    protected void checkIndexes(String message){
        LOGGER.info(this.getClass().getSimpleName() +": Checking indexes " + message);
        List<DBObject>  indexes = getCollection().getIndexInfo();
        int indexCount = indexes.size();
        int counter = 1;
        for (DBObject index : indexes){
            LOGGER.info(this.getClass().getSimpleName() +": Index(" + counter++ + "/" + indexCount + "):" +index);
        }
    }

    protected DBObject generateKeysDBObject(String... keys) {
        DBObject dbKeys = new BasicDBObject();
        for (String key: keys){
            dbKeys.put(key, 1);
        }
        return dbKeys;
    }

    @Override
    public void update(T o, T n) {
        getCollection().update(toDBObject.apply(o), toDBObject.apply(n));
    }
}
