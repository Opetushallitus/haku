package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;

import java.util.Map;

/**
 *
 * @author hannu
 */
public class FormModelDAOMongoImpl extends AbstractDAOMongoImpl implements FormModelDAO {

    public static final String FORM_MODEL = "form_model";

    @Override
    public String getCollectionName() {
        return "haku";
    }

    @Override
    public FormModel find() {
        return (FormModel)getCollection().findOne();
    }

    @Override
    public void insert(FormModel formModel) {
        DBObject formModelMap = new BasicDBObject();
        formModelMap.put(FORM_MODEL, formModel);
        getCollection().insert(formModelMap);
    }
}
