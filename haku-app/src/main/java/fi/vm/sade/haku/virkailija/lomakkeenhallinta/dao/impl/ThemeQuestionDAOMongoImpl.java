package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import fi.vm.sade.haku.oppija.common.dao.AbstractDAOMongoImpl;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
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
import static com.mongodb.QueryOperators.*;

@Service("themeQuestionDAOMongoImpl")
public class ThemeQuestionDAOMongoImpl extends AbstractDAOMongoImpl<ThemeQuestion> implements ThemeQuestionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeQuestionDAOMongoImpl.class);

    private static final String FIELD_ID = "_id";
    private static final String FIELD_APPLICATION_SYSTEM_ID = "applicationSystemId";
    private static final String FIELD_LO_ID = "learningOpportunityId";
    private static final String FIELD_OWNER_OIDS= "ownerOrganizationOids";
    private static final String FIELD_THEME= "theme";
    private static final String FIELD_STATE = "state";
    private static final String FIELD_APPLICATION_OPTION = "learningOpportunityId";

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
        LOGGER.debug("Executing with query:" + query.toString());
        List <ThemeQuestion> themeQuestions =  executeQuery(query);
        LOGGER.debug("Found: " + themeQuestions.size());
        if (themeQuestions.size() == 1) {
            return themeQuestions.get(0);
        }
        throw new ResourceNotFoundException("No ThemeQuestion found with id " + id);
    }

    public List<ThemeQuestion> query(final ThemeQuestionQueryParameters parameters){
        return executeQuery(buildQuery(parameters));
    }

    @Override
    public List<String> queryApplicationOptionsIn(ThemeQuestionQueryParameters parameters) {
        List<Object> distinctApplicationOptions = getCollection().distinct(FIELD_APPLICATION_OPTION, buildQuery(parameters));
        LOGGER.debug("Got "+ distinctApplicationOptions.size() + " application options ");
        ArrayList<String> results = new ArrayList<String>();
        for (Object value :distinctApplicationOptions){
            LOGGER.debug("Got option " + value);
            results.add((String) value);
        }
        return results;
    }

    private final DBObject buildQuery(final ThemeQuestionQueryParameters parameters){
        BasicDBObject query = new BasicDBObject();
        if (null != parameters.getApplicationSystemId()){
            query.append(FIELD_APPLICATION_SYSTEM_ID, parameters.getApplicationSystemId());
        }
        if (null != parameters.getLearningOpportunityId()){
            query.append(FIELD_LO_ID, parameters.getLearningOpportunityId());
        }
        if (null != parameters.getOrganizationId()){
            query.append(FIELD_OWNER_OIDS, parameters.getOrganizationId());
        }
        if (null != parameters.getTheme()){
            query.append(FIELD_THEME, parameters.getTheme());
        }
        if (parameters.searchDeleted()) {
            query.append(FIELD_STATE, ThemeQuestion.State.DELETED);
        }else {
            Object[] states = {ThemeQuestion.State.ACTIVE.toString(), ThemeQuestion.State.LOCKED.toString()};
            query.append(FIELD_STATE, new BasicDBObject(IN, states));
        }
        return query;
    }


}
