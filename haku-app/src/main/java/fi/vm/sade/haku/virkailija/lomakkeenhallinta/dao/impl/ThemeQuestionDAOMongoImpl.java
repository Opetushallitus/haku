package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import fi.vm.sade.haku.oppija.common.dao.AbstractDAOMongoImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.ThemeQuestionQueryParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.DBObjectToThemeQuestionFunction;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.ThemeQuestionToDBObjectFunction;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.ThemeQuestion;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("themeQuestionDAOMongoImpl")
public class ThemeQuestionDAOMongoImpl extends AbstractDAOMongoImpl<ThemeQuestion> implements ThemeQuestionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeQuestionDAOMongoImpl.class);

    private static final String FIELD_ID = "_id";
    private static final String FIELD_APPLICATION_SYSTEM_ID = "applicationSystemId";
    private static final String FIELD_LOP_ID = "learningOpportunityProviderId";

    private static final String collectionName = "themequestion";

    @Autowired
    public ThemeQuestionDAOMongoImpl(DBObjectToThemeQuestionFunction dbObjectToThemeQuestionFunction, ThemeQuestionToDBObjectFunction themeQuestionToDBObjectFunction) {
        super(dbObjectToThemeQuestionFunction, themeQuestionToDBObjectFunction);
    }

    @Override
    protected String getCollectionName() { return collectionName; }

    private List<ThemeQuestion> executeQuery(DBObject dbObject) {
        final DBCursor dbCursor = getCollection().find(dbObject);
        return Lists.newArrayList(Iterables.transform(dbCursor, fromDBObject));
    }

    @Override
    public ThemeQuestion findById(String id) {
        LOGGER.debug("findById: " + id);
        BasicDBObject query = new BasicDBObject(FIELD_ID, new ObjectId(id));
        // TODO consistency check
        LOGGER.debug("with query:" + query.toString());
        return executeQuery(query).get(0);
    }

    public List<ThemeQuestion> query(ThemeQuestionQueryParameters parameters){
        //TODO consistence checks
        BasicDBObject query = new BasicDBObject();
        if (null != parameters.getApplicationSystemId()){
            query.append(FIELD_APPLICATION_SYSTEM_ID, parameters.getApplicationSystemId());
        }

        if (null != parameters.getLearningOpportunityProviderId()){
            query.append(FIELD_LOP_ID, parameters.getLearningOpportunityProviderId());
        }
        return executeQuery(query);
    }


}
