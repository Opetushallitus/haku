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
