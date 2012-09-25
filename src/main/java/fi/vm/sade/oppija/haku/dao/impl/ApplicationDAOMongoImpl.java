package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.service.Application;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl implements ApplicationDAO {

    @Override
    public List<Application> findAllByUserId(String userId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Application find(String userId, String applicationId) {
        Application application = new Application(userId, applicationId);

        DBObject dbObject = new BasicDBObject();
        dbObject.put("userId", userId);
        dbObject.put("applicationId", applicationId);

        application.setApplicationData((Map<String, Map<String, String>>)getCollection().findOne().toMap().get("applicationData"));

        return application;
    }

    @Override
    public void update(Application application) {
        DBObject query = new BasicDBObject();
        query.put("userId", application.getUserId());
        query.put("applicationId", application.getApplicationId());

        DBObject newApplication = new BasicDBObject();
        newApplication.put("userId", application.getUserId());
        newApplication.put("applicationId", application.getApplicationId());
        newApplication.put("applicationData", application.getApplicationData());

        getCollection().update(query, newApplication, true, false);

    }

    @Override
    protected String getCollectionName() {
        return "hakemus";
    }
}
