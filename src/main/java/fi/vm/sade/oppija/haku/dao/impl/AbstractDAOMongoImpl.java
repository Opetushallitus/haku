package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.dao.DBFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDAOMongoImpl {

    @Autowired
    protected DBFactoryBean factoryBean;
    protected DB db;

    @PostConstruct
    public void init() throws Exception {
        this.db = factoryBean.getObject();
    }

    public DBCollection getCollection() {
        return db.getCollection(getCollectionName());
    }

    public abstract String getCollectionName();

    @SuppressWarnings("unchecked")
    protected Map<String, Object> toMap(DBObject rawApplicationPeriod) {
        Map<String, Object> applicationPeriod = new HashMap<String, Object>();
        if (rawApplicationPeriod != null) {
            applicationPeriod = (Map<String, Object>) rawApplicationPeriod.toMap();
        }
        return Collections.unmodifiableMap(applicationPeriod);
    }
}
