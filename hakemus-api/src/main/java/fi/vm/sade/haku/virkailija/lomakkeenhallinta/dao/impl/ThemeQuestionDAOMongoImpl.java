package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
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

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.QueryOperators.IN;

@Service("themeQuestionDAOMongoImpl")
public class ThemeQuestionDAOMongoImpl extends AbstractDAOMongoImpl<ThemeQuestion> implements ThemeQuestionDAO {

    //@Value("${mongodb.ensureIndex:true}")
    private boolean ensureIndex = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeQuestionDAOMongoImpl.class);

    private static final String FIELD_ID = "_id";
    private static final String FIELD_APPLICATION_SYSTEM_ID = "applicationSystemId";
    private static final String FIELD_OWNER_OIDS= "ownerOrganizationOids";
    private static final String FIELD_THEME= "theme";
    private static final String FIELD_STATE = "state";
    private static final String FIELD_APPLICATION_OPTION = "learningOpportunityId";
    private static int QUERY =0;
    private static int HINT =1;

    private static final String collectionName = "themequestion";

    @Autowired
    public ThemeQuestionDAOMongoImpl(DBObjectToThemeQuestionFunction dbObjectToThemeQuestionFunction, ThemeQuestionToDBObjectFunction themeQuestionToDBObjectFunction) {
        super(dbObjectToThemeQuestionFunction, themeQuestionToDBObjectFunction);
    }

    @Override
    protected String getCollectionName() { return collectionName; }

    private List<ThemeQuestion> executeQuery(DBObject[] queryParam) {
        LOGGER.debug("Executing with query: " + queryParam[QUERY] + " with hint: " +queryParam[HINT]);
        final DBCursor dbCursor = getCollection().find(queryParam[QUERY]);
        if (ensureIndex && null != queryParam[HINT]) {
            dbCursor.hint(queryParam[HINT]);
        }
        try {
            return Lists.newArrayList(Iterables.transform(dbCursor, fromDBObject));
        }catch (MongoException mongoException){
            LOGGER.error("Got error "+ mongoException.getMessage() +" with query: " + queryParam[QUERY] + " using hint: " +queryParam[HINT]);
            throw mongoException;
        }
    }

    @Override
    public ThemeQuestion findById(String id) {
        LOGGER.debug("findById: " + id);
        DBObject queryParam = new BasicDBObject(FIELD_ID, new ObjectId(id));
        LOGGER.debug("Executing with query: " + queryParam.toString());
        List <ThemeQuestion> themeQuestions =  executeQuery(new DBObject[]{queryParam, null});
        LOGGER.debug("Found: " + themeQuestions.size());
        if (themeQuestions.size() == 1) {
            return themeQuestions.get(0);
        }
        throw new ResourceNotFoundException("No ThemeQuestion found with id " + id);
    }

    public List<ThemeQuestion> query(final ThemeQuestionQueryParameters parameters){
        DBObject[] queryParameters = buildQuery(parameters);
        return executeQuery(queryParameters);
    }

    @Override
    public List<String> queryApplicationOptionsIn(ThemeQuestionQueryParameters parameters) {
        List<Object> distinctApplicationOptions = getCollection().distinct(FIELD_APPLICATION_OPTION, buildQuery(parameters)[0]);
        LOGGER.debug("Got "+ distinctApplicationOptions.size() + " application options ");
        ArrayList<String> results = new ArrayList<String>();
        for (Object value : distinctApplicationOptions){
            LOGGER.debug("Got option " + value);
            results.add((String) value);
        }
        return results;
    }

    private final DBObject[] buildQuery(final ThemeQuestionQueryParameters parameters){
        BasicDBObject query = new BasicDBObject();
        BasicDBObject hint = new BasicDBObject();
        if (null != parameters.getApplicationSystemId()){
            query.append(FIELD_APPLICATION_SYSTEM_ID, parameters.getApplicationSystemId());
            hint.put(FIELD_APPLICATION_SYSTEM_ID, 1);
        }

        if (parameters.searchDeleted()) {
            query.append(FIELD_STATE, ThemeQuestion.State.DELETED);
        }else {
            Object[] states = {ThemeQuestion.State.ACTIVE.toString(), ThemeQuestion.State.LOCKED.toString()};
            query.append(FIELD_STATE, new BasicDBObject(IN, states));
        }
        hint.put(FIELD_STATE, 1);

        if (null != parameters.getOrganizationId()){
            query.append(FIELD_OWNER_OIDS, parameters.getOrganizationId());
            hint.put(FIELD_OWNER_OIDS, 1);
        }

        if (null != parameters.getTheme()){
            query.append(FIELD_THEME, parameters.getTheme());
            hint.put(FIELD_THEME, 1);
        }

        if (null != parameters.getLearningOpportunityId()){
            query.append(FIELD_APPLICATION_OPTION, parameters.getLearningOpportunityId());
            hint.put(FIELD_APPLICATION_OPTION, 1);
        }
        return new DBObject[]{query,hint};
    }


    @PostConstruct
    public void configure() {
        if (!ensureIndex) {
            return;
        }

        checkIndexes("before ensures");

        ensureIndex("index_applicationsystem_ownerid", FIELD_APPLICATION_SYSTEM_ID, FIELD_STATE, FIELD_OWNER_OIDS, FIELD_APPLICATION_OPTION);
        ensureIndex("index_applicationsystem_theme", FIELD_APPLICATION_SYSTEM_ID, FIELD_STATE, FIELD_THEME, FIELD_APPLICATION_OPTION);
        ensureIndex("index_applicationsystem_ao", FIELD_APPLICATION_SYSTEM_ID, FIELD_STATE, FIELD_APPLICATION_OPTION);

        ensureIndex("index_owner", FIELD_STATE, FIELD_OWNER_OIDS, FIELD_APPLICATION_OPTION);
        ensureIndex("index_theme", FIELD_STATE, FIELD_THEME, FIELD_APPLICATION_OPTION);
        ensureIndex("index_ao", FIELD_STATE, FIELD_APPLICATION_OPTION);
        checkIndexes("after ensures");
    }
}
