package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.*;
import fi.vm.sade.oppija.haku.dao.DBFactoryBean;
import fi.vm.sade.oppija.haku.dao.SampleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 8/31/123:06 PM}
 * @since 1.1
 */
@Service
public class SampleDAOImpl implements SampleDAO {

    @Autowired
    private DBFactoryBean factoryBean;
    private DB db;

    @PostConstruct
    public void init() throws Exception {
        this.db = factoryBean.getObject();
    }

    @Override
    public void insert(Map<String, String> map) {
        getCollection().insert(new BasicDBObject(map));
    }

    @Override
    public Map listAll() {
        final DBCursor dbObjects = getCollection().find();
        Map<String, String> map = new HashMap<String, String>();
        while (dbObjects.hasNext()) {
            DBObject next = dbObjects.next();
            map.putAll(next.toMap());
        }
        return Collections.unmodifiableMap(map);
    }

    public DBCollection getCollection() {
        return db.getCollection("sample");
    }
}
