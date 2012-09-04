package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import fi.vm.sade.oppija.haku.dao.DBFactoryBean;
import fi.vm.sade.oppija.haku.dao.FormDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class FormDAOMongoImpl extends AbstractDAOMongoImpl implements FormDAO {


    @Override
    public void insert(Map<Object, Object> map) {
        getCollection().insert(new BasicDBObject(map));
    }

    @Override
    public Map listAll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
