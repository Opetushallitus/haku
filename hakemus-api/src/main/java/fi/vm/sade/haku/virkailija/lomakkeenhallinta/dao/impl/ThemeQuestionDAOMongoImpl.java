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
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.QueryOperators.EXISTS;
import static com.mongodb.QueryOperators.IN;
import static com.mongodb.QueryOperators.NE;
import static com.mongodb.QueryOperators.OR;

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
    private static final String FIELD_ORDINAL = "ordinal";
    private static final String FIELD_APPLICATION_OPTION = "learningOpportunityId";
    private static final String FIELD_PARENT_ID = "parentId";
    private static final String FIELD_TARGET_IS_GROUP = "targetIsGroup";
    private static final String FIELD_ATTACHMENT_REQUESTS = "attachmentRequests";
    private static int PARAM_QUERY =0;
    private static int PARAM_HINT =1;
    private static int PARAM_SORT_BY = 2;
    private static int PARAM_KEYS = 3;

    @Autowired
    public ThemeQuestionDAOMongoImpl(DBObjectToThemeQuestionFunction dbObjectToThemeQuestionFunction, ThemeQuestionToDBObjectFunction themeQuestionToDBObjectFunction) {
        super("themequestion", dbObjectToThemeQuestionFunction, themeQuestionToDBObjectFunction);
    }

    private DBCursor executeQuery(DBObject[] queryParam){
        final DBCursor dbCursor = getCollection().find(queryParam[PARAM_QUERY], queryParam[PARAM_KEYS]);
        if (null != queryParam[PARAM_SORT_BY]){
            dbCursor.sort(queryParam[PARAM_SORT_BY]);
        }
        if (ensureIndex && null != queryParam[PARAM_HINT]) {
            dbCursor.hint(queryParam[PARAM_HINT]);
        }
        return dbCursor;
    }

    private List<ThemeQuestion> queryThemeQuestions(DBObject[] queryParam) {
        LOGGER.debug("Executing with query: " + queryParam[PARAM_QUERY] + " with hint: " +queryParam[PARAM_HINT]);
        final DBCursor dbCursor = executeQuery(queryParam);
        try {
            return Lists.newArrayList(Iterables.transform(dbCursor, fromDBObject));
        }catch (MongoException mongoException){
            LOGGER.error("Got error "+ mongoException.getMessage() +" with query: " + queryParam[PARAM_QUERY] + " using hint: " +queryParam[PARAM_HINT]);
            throw mongoException;
        }
    }

    @Override
    public ThemeQuestion findById(String id) {
        LOGGER.debug("findById: " + id);
        DBObject queryParam = new BasicDBObject(FIELD_ID, new ObjectId(id));
        LOGGER.debug("Executing with query: " + queryParam.toString());
        List <ThemeQuestion> themeQuestions =  queryThemeQuestions(new DBObject[] { queryParam, null, null, null });
        LOGGER.debug("Found: " + themeQuestions.size());
        if (themeQuestions.size() == 1) {
            return themeQuestions.get(0);
        }
        throw new ResourceNotFoundException("No ThemeQuestion found with id " + id);
    }

    @Override
    public List<ThemeQuestion> findByParentId(String parentId) {
        LOGGER.debug("findByParentId: " + parentId);
        DBObject queryParam = new BasicDBObject(FIELD_PARENT_ID, parentId);
        LOGGER.debug("Executing with query: " + queryParam.toString());
        List <ThemeQuestion> themeQuestions =  queryThemeQuestions(new DBObject[] { queryParam, null, null, null });
        LOGGER.debug("Found: " + themeQuestions.size());
        return themeQuestions;
    }

    public List<ThemeQuestion> query(final ThemeQuestionQueryParameters parameters){
        return queryThemeQuestions(buildQuery(parameters));
    }

    @Override
    public List<String> queryApplicationOptionsIn(ThemeQuestionQueryParameters parameters) {
        List<Object> distinctApplicationOptions = getCollection().distinct(FIELD_APPLICATION_OPTION, buildQuery(parameters)[0]);
        LOGGER.debug("Got " + distinctApplicationOptions.size() + " application options ");
        ArrayList<String> results = new ArrayList<String>();
        for (Object value : distinctApplicationOptions){
            LOGGER.debug("Got option " + value);
            results.add((String) value);
        }
        return results;
    }

    @Override
    public void setOrdinal(String themeQuestionId, Integer newOrdinal) {
        DBObject update = new BasicDBObject(FIELD_ORDINAL, newOrdinal);
        set(themeQuestionId, update);
        LOGGER.debug("ThemeQuestion "+ themeQuestionId + " ordinal updated to " + newOrdinal);
    }

    @Override
    public void delete(String themeQuestionId) {
        DBObject update = new BasicDBObject(FIELD_STATE, ThemeQuestion.State.DELETED.toString());
        update.put(FIELD_ORDINAL, 99);
        set(themeQuestionId, update);
        LOGGER.debug("ThemeQuestion "+ themeQuestionId + " deleted");
    }

    @Override
    public Integer getMaxOrdinal(String applicationSystemId, String learningOpportunityId, String themeId) {
        final ThemeQuestionQueryParameters tqqp = new ThemeQuestionQueryParameters();
        tqqp.setApplicationSystemId(applicationSystemId);
        tqqp.setLearningOpportunityId(learningOpportunityId);
        tqqp.setTheme(themeId);
        tqqp.addSortBy(FIELD_ORDINAL, tqqp.SORT_DESCENDING);
        final DBObject[] queryParam = buildQuery(tqqp);
        queryParam[PARAM_KEYS] = new BasicDBObject(FIELD_ORDINAL, 1);
        final DBCursor dbCursor = executeQuery(queryParam);
        dbCursor.limit(1);
        try {
            if (!dbCursor.hasNext()) {
                return null;
            }
            return (Integer) dbCursor.next().get(FIELD_ORDINAL);
        } catch (MongoException mongoException) {
            LOGGER.error("Got error "+ mongoException.getMessage() +" with query: " + queryParam[PARAM_QUERY] + " using hint: " +queryParam[PARAM_HINT] + " and keys: " +queryParam[PARAM_KEYS]);
            throw mongoException;
        }
    }

    @Override
    public Integer getMaxOrdinalOfChildren(String applicationSystemId, String learningOpportunityId, String themeId, String parentId) {
        final ThemeQuestionQueryParameters tqqp = new ThemeQuestionQueryParameters();
        tqqp.setApplicationSystemId(applicationSystemId);
        tqqp.setLearningOpportunityId(learningOpportunityId);
        tqqp.setTheme(themeId);
        tqqp.setParentThemeQuestionId(parentId);
        tqqp.addSortBy(FIELD_ORDINAL, tqqp.SORT_DESCENDING);
        final DBObject[] queryParam = buildQuery(tqqp);
        queryParam[PARAM_KEYS] = new BasicDBObject(FIELD_ORDINAL, 1);
        final DBCursor dbCursor = executeQuery(queryParam);
        dbCursor.limit(1);
        try {
            if (!dbCursor.hasNext()) {
                return null;
            }
            return (Integer) dbCursor.next().get(FIELD_ORDINAL);
        } catch (MongoException mongoException) {
            LOGGER.error("Got error "+ mongoException.getMessage() +" with query: " + queryParam[PARAM_QUERY] + " using hint: " +queryParam[PARAM_HINT] + " and keys: " +queryParam[PARAM_KEYS]);
            throw mongoException;
        }
    }

    private void set(String themeQuestionId, DBObject update){
        DBObject find = new BasicDBObject(FIELD_ID, new ObjectId(themeQuestionId));
        DBObject setUpdate = new BasicDBObject("$set", update);
        try {
            getCollection().findAndModify(find, setUpdate);
        }
        catch(MongoException mongoException){
            LOGGER.error("Got error " + mongoException.getMessage() + " while updating ThemeQuestion: "+themeQuestionId +" with data: " + setUpdate);
            throw mongoException;
        }
    }

    @Override
    public Boolean validateLearningOpportunityAndTheme(String learningOpportunityId, String themeId, String... themeQuestionIds) {
        BasicDBObject query = new BasicDBObject();
        query.append(FIELD_APPLICATION_OPTION, learningOpportunityId);
        query.append(FIELD_THEME, themeId);
        ObjectId[] objectIds = new ObjectId[themeQuestionIds.length];
        for (int i= 0; i < themeQuestionIds.length; i++){
            objectIds[i] = new ObjectId(themeQuestionIds[i]);
        }
        query.append(FIELD_ID, new BasicDBObject(IN, objectIds));
        int count = -1;
        try {
            //TODO: =RS= Need support for index hints
            count = getCollection().find(query).count();
        }catch (MongoException mongoException){
            LOGGER.error("Validation query failed for AO: {}, Theme: {}, QuestionIds: {}. Got: {}", learningOpportunityId, themeId, themeQuestionIds, mongoException.getMessage());
            throw mongoException;
        }
        LOGGER.debug("Validating AO: {}, Theme: {}, QuestionIds: {}. Expecting: {}. Got: {}", learningOpportunityId, themeId, themeQuestionIds, themeQuestionIds.length, count);
        return count == themeQuestionIds.length;
    }

    private final DBObject[] buildQuery(final ThemeQuestionQueryParameters parameters){
        BasicDBObject query = new BasicDBObject();
        BasicDBObject hint = new BasicDBObject();
        BasicDBObject sortBy = null;
        BasicDBObject keys = null;
        if (null != parameters.getApplicationSystemId()){
            query.append(FIELD_APPLICATION_SYSTEM_ID, parameters.getApplicationSystemId());
            hint.put(FIELD_APPLICATION_SYSTEM_ID, 1);
        }

        if (parameters.searchDeleted()) {
            query.append(FIELD_STATE, ThemeQuestion.State.DELETED.toString());
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

        if (parameters.isSetParentThemeQuestionId()){
            query.append(FIELD_PARENT_ID, parameters.getParentThemeQuestionId());
            hint.put(FIELD_PARENT_ID, 1);
        }

        if (null != parameters.queryGroups()){
            if (parameters.queryGroups()) {
                query.append(FIELD_TARGET_IS_GROUP, true);
            } else {
                query.append(OR, new DBObject[] {
                  new BasicDBObject(FIELD_TARGET_IS_GROUP, false),
                  new BasicDBObject(FIELD_TARGET_IS_GROUP, new BasicDBObject(EXISTS, false))
                });
            }
            hint.put(FIELD_TARGET_IS_GROUP, 1);
        }

        if (null != parameters.onlyWithAttachmentRequests() && parameters.onlyWithAttachmentRequests()){
            query.append(FIELD_ATTACHMENT_REQUESTS, new BasicDBObject(NE, null));
            hint.put(FIELD_ATTACHMENT_REQUESTS, 1);
        }

        if (parameters.getSortBy().size() > 0){
            sortBy = new BasicDBObject();
            for (Pair<String, Integer> sortKey: parameters.getSortBy()){
                String field = sortKey.getKey();
                Integer order = sortKey.getValue();
                sortBy.append(field,order);
            }
        }
        return new DBObject[]{query, hint, sortBy, keys};
    }


    @PostConstruct
    public void configure() {
        if (!ensureIndex) {
            return;
        }

        checkIndexes("before ensures");

        //TODO =RS= Add parent data to indexes

        ensureIndex("index_applicationsystem_ownerid", FIELD_APPLICATION_SYSTEM_ID, FIELD_STATE, FIELD_OWNER_OIDS, FIELD_APPLICATION_OPTION);
        ensureIndex("index_applicationsystem_theme", FIELD_APPLICATION_SYSTEM_ID, FIELD_STATE, FIELD_THEME, FIELD_APPLICATION_OPTION);
        ensureIndex("index_applicationsystem_ao", FIELD_APPLICATION_SYSTEM_ID, FIELD_STATE, FIELD_APPLICATION_OPTION);

        ensureIndex("index_owner", FIELD_STATE, FIELD_OWNER_OIDS, FIELD_APPLICATION_OPTION);
        ensureIndex("index_theme", FIELD_STATE, FIELD_THEME, FIELD_APPLICATION_OPTION);
        ensureIndex("index_ao", FIELD_STATE, FIELD_APPLICATION_OPTION);
        checkIndexes("after ensures");
    }
}
