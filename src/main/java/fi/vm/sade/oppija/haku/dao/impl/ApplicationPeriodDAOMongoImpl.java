package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.dao.ApplicationPeriodDAO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApplicationPeriodDAOMongoImpl extends AbstractDAOMongoImpl implements ApplicationPeriodDAO {

    @Override
    public void insert(Map<String, Object> map) {
        getCollection().insert(new BasicDBObject(map));
    }

    @Override
    public Map<String, Object> find(String applicationPeriodId) {
        DBObject o = new BasicDBObject();
        o.put("id", applicationPeriodId);
        DBObject rawApplicationPeriod = getCollection().findOne(o);
        return toMap(rawApplicationPeriod);
    }

    @Override
    public Map<String, Map<String, Object>> findAll() {
        final DBCursor dbObjects = getCollection().find();
        final List<DBObject> dbObjects1 = dbObjects.toArray();
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        for (DBObject dbObject : dbObjects1) {
            final Map<String, Object> haku = toMap(dbObject);
            map.put(haku.get("id").toString(), haku);
        }
        return map;
    }

    @Override
    public Map<String, Object> findForm(String applicationPeriod, String form) {
        DBObject queryObject = new BasicDBObject();
        queryObject.put("id", applicationPeriod);
        queryObject.put("form.id", form);

        return (Map<String, Object>) getCollection().findOne(queryObject).get("form");
    }

    @Override
    public String getCollectionName() {
        return "haku";
    }
}
