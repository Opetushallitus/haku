package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.BasicDBObject;
import fi.vm.sade.oppija.haku.dao.ApplicationPeriodDAO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApplicationPeriodDAOMongoImpl extends AbstractDAOMongoImpl implements ApplicationPeriodDAO {

    @Override
    public void insert(Map<Object, Object> map) {
        getCollection().insert(new BasicDBObject(map));
    }

}
