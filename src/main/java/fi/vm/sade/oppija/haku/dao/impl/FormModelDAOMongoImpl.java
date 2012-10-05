package fi.vm.sade.oppija.haku.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.converter.FormModelToBasicDBObject;
import fi.vm.sade.oppija.haku.converter.JsonStringToFormModelConverter;
import fi.vm.sade.oppija.haku.converter.MapToFormModelConverter;
import fi.vm.sade.oppija.haku.dao.FormModelDAO;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.service.FormModelHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author hannu
 */
@Service("formModelDAOMongoImpl")
public class FormModelDAOMongoImpl extends AbstractDAOMongoImpl implements FormModelDAO {

    private static final Logger LOG = LoggerFactory.getLogger(FormModelDAOMongoImpl.class);
    private static final String COLLECTION_FORM_MODEL = "haku";

    @Autowired
    FormModelHolder holder;

    private final FormModelToBasicDBObject toDbObject = new FormModelToBasicDBObject();
    private final MapToFormModelConverter mapToFormModelConverter = new MapToFormModelConverter();

    @PostConstruct
    public void init() {
        super.init();
        try {
            holder.updateModel(find());
        } catch (Exception ignored) {
            LOG.warn("No model found ! ");
        }
    }

    @Override
    public String getCollectionName() {
        return COLLECTION_FORM_MODEL;
    }

    @Override
    public FormModel find() {
        final DBObject one = getCollection().findOne();
        return mapToFormModelConverter.convert(one.toMap());
    }

    @Override
    public void insert(FormModel formModel) {
        final BasicDBObject basicDBObject = toDbObject.convert(formModel);
        dropAndInsert(basicDBObject);
    }


    @Override
    public void insertModelAsJsonString(final String json) {
        LOG.debug("with content " + json);

        //we do this via model, as this quarantees validness of data
        final BasicDBObject convert1 = validateJson(json);
        dropAndInsert(convert1);
        holder.updateModel(find());
    }

    private synchronized void dropAndInsert(BasicDBObject convert1) {
        getCollection().drop();
        getCollection().insert(convert1);
    }

    private BasicDBObject validateJson(String json) {
        final FormModel converted = new JsonStringToFormModelConverter().convert(json);
        return toDbObject.convert(converted);
    }


    @Override
    public void delete(FormModel formModel) {
        final BasicDBObject basicDBObject = toDbObject.convert(formModel);
        getCollection().remove(basicDBObject);
    }
}
