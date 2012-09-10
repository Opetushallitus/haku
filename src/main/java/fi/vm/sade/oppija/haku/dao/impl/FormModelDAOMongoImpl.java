package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.domain.FormModel;
import org.springframework.stereotype.Service;

/**
 * @author hannu
 */
@Service
public class FormModelDAOMongoImpl extends AbstractDAOMongoImpl implements FormModelDAO {

    public static final String FORM_MODEL = "form_model";

    @Override
    public String getCollectionName() {
        return "haku";
    }

    @Override
    public FormModel find() {
        return (FormModel) getCollection().findOne().get(FORM_MODEL);
    }

    @Override
    public void insert(FormModel formModel) {
        DBObject formModelMap = new BasicDBObject();
        formModelMap.put(FORM_MODEL, formModel);
        getCollection().insert(formModelMap);
    }
}
