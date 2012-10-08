package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.dao.ApplicationDAO;
import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl implements ApplicationDAO {

    @Override
    public void update(Hakemus hakemus) {
        DBObject query = new BasicDBObject();
        query.put("hakemusId", hakemus.getHakemusId().asKey());

        DBObject newApplication = new BasicDBObject();
        newApplication.put("userid", hakemus.getHakemusId().getUserId());
        newApplication.put("hakemusId", hakemus.getHakemusId().asKey());
        newApplication.put("hakemusData", hakemus.getValues());

        getCollection().update(query, newApplication, true, false);

    }

    @Override
    protected String getCollectionName() {
        return "hakemus";
    }

    @Override
    public Hakemus find(HakemusId hakemusId) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("hakemusid", hakemusId.asKey());
        final DBObject one = getCollection().findOne();
        Map<String, String> map = new HashMap<String, String>();
        if (one != null) {
            map = (Map<String, String>) one.toMap().get("hakemusData");
        }
        return new Hakemus(hakemusId, map);
    }
}
