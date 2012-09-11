package fi.vm.sade.oppija.haku.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.service.FormModelHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

/**
 * @author hannu
 */
@Service("formModelDAOMongoImpl")
public class FormModelDAOMongoImpl extends AbstractDAOMongoImpl implements FormModelDAO {

    @Autowired
    FormModelHolder holder;

    @PostConstruct
    public void init() {
        holder.updateModel(find());
    }

    @Override
    public String getCollectionName() {
        return "haku";
    }

    @Override
    public FormModel find() {
        final DBObject one = getCollection().findOne();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(one.toMap(), FormModel.class);
    }

    private Map serialize(FormModel model, Class<Map> dbObjectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(model, dbObjectClass);
    }


    @Override
    public void insert(FormModel formModel) {
        final BasicDBObject basicDBObject = toBasicDbObject(formModel);
        getCollection().insert(basicDBObject);

    }

    private BasicDBObject toBasicDbObject(FormModel formModel) {
        try {
            Map formModelMap = serialize(formModel, Map.class);
            return new BasicDBObject(formModelMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(FormModel formModel) {
        final BasicDBObject o = toBasicDbObject(formModel);
        getCollection().remove(o);
    }
}
