package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.service.Application;
import org.springframework.stereotype.Service;

import java.util.List;

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

        application.setApplicationData(getCollection().findOne().toMap());

        return application;
    }

    @Override
    public void insert(Application application) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("userId", application.getUserId());
        dbObject.put("applicationId", application.getApplicationId());
        dbObject.put("applicationData", application.getApplicationData());
        getCollection().insert(dbObject);
    }

    @Override
    public void update(Application application) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String getCollectionName() {
        return "hakemus";
    }
}
