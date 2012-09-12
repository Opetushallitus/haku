package fi.vm.sade.oppija.haku.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.haku.converter.FormModelToBasicDBObject;
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

    private final static Logger log = LoggerFactory.getLogger(FormModelDAOMongoImpl.class);

    @Autowired
    FormModelHolder holder;

    private FormModelToBasicDBObject converter;

    @PostConstruct
    public void init() throws Exception {
        super.init();
        try {
            holder.updateModel(find());
        } catch (Exception ignored) {
            log.warn("No model found ! ");
        }
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

    @Override
    public void insert(FormModel formModel) {
        converter = new FormModelToBasicDBObject();
        final BasicDBObject basicDBObject = converter.convert(formModel);
        getCollection().insert(basicDBObject);

    }


    @Override
    public void insertModelAsJsonString(StringBuilder builder) {
        getCollection().drop();
        final String json = builder.toString();
        log.debug("with content " + json);
        getCollection().insert((DBObject) JSON.parse(json));
        holder.updateModel(find());
    }


    @Override
    public void delete(FormModel formModel) {
        final BasicDBObject basicDBObject = converter.convert(formModel);
        getCollection().remove(basicDBObject);
    }
}
