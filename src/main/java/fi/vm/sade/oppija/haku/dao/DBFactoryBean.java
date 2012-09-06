package fi.vm.sade.oppija.haku.dao;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author jukka
 * @version 8/31/123:23 PM}
 * @since 1.1
 */
public class DBFactoryBean implements FactoryBean<DB> {
    protected Mongo mongo;
    private String name;

    @PostConstruct
    public void init() {
        mongo.setWriteConcern(WriteConcern.SAFE);
    }

    @PreDestroy
    public void shutDown() {
        mongo.close();
    }

    @Override
    public DB getObject() throws Exception {
        return mongo.getDB(name);
    }

    @Override
    public Class<?> getObjectType() {
        return DB.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Required
    public void setMongo(Mongo mongo) {
        this.mongo = mongo;
    }

    @Required
    public void setName(String name) {
        this.name = name;
    }
}