package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import fi.vm.sade.oppija.haku.dao.DBFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class AbstractDAOMongoImpl {

    @Autowired
    protected DBFactoryBean factoryBean;
    protected DB db;

    @PostConstruct
    protected void init() {
        this.db = factoryBean.getObject();
    }

    protected DBCollection getCollection() {
        return db.getCollection(getCollectionName());
    }

    protected abstract String getCollectionName();

}
