package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.dao.ApplicationPeriodDAO;
import org.springframework.stereotype.Service;

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
    public String getCollectionName() {
        return "haku";
    }
}
