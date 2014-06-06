/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.haku.oppija.hakemus.it.dao.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.*;
import fi.vm.sade.haku.oppija.common.dao.AbstractDAOMongoImpl;
import fi.vm.sade.haku.oppija.hakemus.converter.ApplicationToDBObjectFunction;
import fi.vm.sade.haku.oppija.hakemus.converter.DBObjectToApplicationFunction;
import fi.vm.sade.haku.oppija.hakemus.converter.DBObjectToMapFunction;
import fi.vm.sade.haku.oppija.hakemus.converter.DBObjectToSearchResultItem;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.service.EncrypterService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.join;

/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl<Application> implements ApplicationDAO {

    @Value("${mongodb.ensureIndex:true}")
    private boolean ensureIndex;

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDAOMongoImpl.class);
    private static final String INDEX_APPLICATION_OID = "index_oid";
    private static final String INDEX_SSN = "index_Henkilotunnus";
    private static final String INDEX_SSN_DIGEST = "index_Henkilotunnus_digest";
    private static final String INDEX_DATE_OF_BIRTH = "index_syntymaaika";
    private static final String INDEX_PERSON_OID = "index_personOid";
    private static final String INDEX_STUDENT_OID = "index_studentOid";
    private static final String INDEX_STATE = "index_state";
    private static final String INDEX_STUDENT_IDENTIFICATION_DONE = "index_studentIdentificationDone";
    private static final String INDEX_SENDING_SCHOOL = "index_lahtokoulu";
    private static final String INDEX_SENDING_CLASS = "index_lahtoluokka";
    private static final String INDEX_SEARCH_NAMES = "index_searchNames";
    private static final String INDEX_REDO_POSTPROCESS = "index_redoPostProcess";

    private static final String FIELD_AO_T = "answers.hakutoiveet.preference%d-Koulutus-id";
    private static final String FIELD_AO_KOULUTUS_ID_T = "answers.hakutoiveet.preference%d-Koulutus-id-aoIdentifier";
    private static final String FIELD_LOP_T = "answers.hakutoiveet.preference%d-Opetuspiste-id";
    private static final String FIELD_LOP_PARENTS_T = "answers.hakutoiveet.preference%d-Opetuspiste-id-parents";
    private static final String FIELD_DISCRETIONARY_T = "answers.hakutoiveet.preference%d-discretionary";
    private static final String FIELD_APPLICATION_OID = "oid";
    private static final String FIELD_APPLICATION_SYSTEM_ID = "applicationSystemId";
    private static final String FIELD_PERSON_OID = "personOid";
    private static final String FIELD_APPLICATION_STATE = "state";
    private static final String FIELD_LAST_AUTOMATED_PROCESSING_TIME = "lastAutomatedProcessingTime";
    private static final String FIELD_SENDING_SCHOOL = "answers.koulutustausta.lahtokoulu";
    private static final String FIELD_SENDING_SCHOOL_PARENTS = "answers.koulutustausta.lahtokoulu-parents";
    private static final String FIELD_SENDING_CLASS = "answers.koulutustausta.lahtoluokka";
    private static final String FIELD_CLASS_LEVEL = "answers.koulutustausta.luokkataso";
    private static final String FIELD_SSN = "answers.henkilotiedot.Henkilotunnus";
    private static final String FIELD_SSN_DIGEST = "answers.henkilotiedot.Henkilotunnus_digest";
    private static final String FIELD_DATE_OF_BIRTH = "answers.henkilotiedot.syntymaaika";
    private static final String FIELD_SEARCH_NAMES = "searchNames";
    private static final String FIELD_RECEIVED = "received";
    private static final String FIELD_UPDATED = "updated";
    private static final String EXISTS = "$exists";
    private static final String OPTION_SPARSE = "sparse";
    private static final String OPTION_NAME = "name";
    private static final String FIELD_STUDENT_OID = "studentOid";
    private static final String FIELD_STUDENT_IDENTIFICATION_DONE = "studentIdentificationDone";
    private static final String FIELD_REDO_POSTPROCESS = "redoPostProcess";
    private static final String REGEX_LINE_BEGIN = "^";
    private final EncrypterService shaEncrypter;
    private final DBObjectToSearchResultItem dbObjectToSearchResultItem;
    private final DBObjectToMapFunction dbObjectToMapFunction;

    private static final Pattern OID_PATTERN = Pattern.compile("((^([0-9]{1,4}\\.){5})|(^))[0-9]{11}$");
    private static final Pattern HETU_PATTERN = Pattern.compile("^[0-3][0-9][0-1][0-9][0-9][0-9][-+Aa][0-9]{3}[0-9a-zA-Z]");
    private static final DateFormat HETU_DATE = new SimpleDateFormat("ddMMyy");
    private static final DateFormat LONG_HETU_DATE = new SimpleDateFormat("ddMMyyyy");
    private static final DateFormat FORM_DATE = new SimpleDateFormat("dd.MM.yyyy");

    @Value("${application.oid.prefix}")
    private String applicationOidPrefix;
    @Value("${user.oid.prefix}")
    private String userOidPrefix;
    private AuthenticationService authenticationService;
    private HakuPermissionService hakuPermissionService;

    @Autowired
    public ApplicationDAOMongoImpl(DBObjectToApplicationFunction dbObjectToHakemusConverter,
                                   ApplicationToDBObjectFunction hakemusToBasicDBObjectConverter,
                                   DBObjectToMapFunction dbObjectToMapFunction,
                                   @Qualifier("shaEncrypter") EncrypterService shaEncrypter,
                                   DBObjectToSearchResultItem dbObjectToSearchResultItem,
                                   AuthenticationService authenticationService,
                                   HakuPermissionService hakuPermissionService) {
        super(dbObjectToHakemusConverter, hakemusToBasicDBObjectConverter);
        this.shaEncrypter = shaEncrypter;
        this.dbObjectToSearchResultItem = dbObjectToSearchResultItem;
        this.dbObjectToMapFunction = dbObjectToMapFunction;
        this.authenticationService = authenticationService;
        this.hakuPermissionService = hakuPermissionService;
    }

    @Override
    protected String getCollectionName() {
        return "application";
    }

    @Override
    public List<Application> findByApplicationSystemAndApplicationOption(String asId, String aoId) {
        ArrayList<DBObject> orgFilter = filterByOrganization();
        DBObject dbObject = QueryBuilder.start().and(queryByPreference(Lists.newArrayList(aoId)).get(),
                newOIdExistDBObject(),
                new BasicDBObject(FIELD_APPLICATION_SYSTEM_ID, asId),
                QueryBuilder.start(FIELD_APPLICATION_STATE).in(Lists.newArrayList(
                        Application.State.ACTIVE.toString(), Application.State.INCOMPLETE.toString())).get(),
                QueryBuilder.start().or(orgFilter.toArray(new DBObject[orgFilter.size()])).get()).get();
        return findApplications(dbObject);
    }

    @Override
    public boolean checkIfExistsBySocialSecurityNumber(String asId, String ssn) {
        if (!Strings.isNullOrEmpty(ssn)) {
            String encryptedSsn = shaEncrypter.encrypt(ssn.toUpperCase());
            DBObject query = QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).is(asId)
                    .and("answers.henkilotiedot." + SocialSecurityNumber.HENKILOTUNNUS_HASH).is(encryptedSsn)
                    .and(FIELD_APPLICATION_OID).exists(true)
                    .and(FIELD_APPLICATION_STATE).notEquals(Application.State.PASSIVE.toString())
                    .get();
            return resultNotEmpty(query);
        }
        return false;
    }

    @Override
    public boolean checkIfExistsBySocialSecurityNumberAndAo(String asId, String ssn, String aoId) {
        if (!Strings.isNullOrEmpty(ssn)) {
            String encryptedSsn = shaEncrypter.encrypt(ssn.toUpperCase());
            DBObject query = QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).is(asId)
                    .and("answers.henkilotiedot." + SocialSecurityNumber.HENKILOTUNNUS_HASH).is(encryptedSsn)
                    .and(FIELD_APPLICATION_OID).exists(true)
                    .and(FIELD_APPLICATION_STATE).notEquals(Application.State.PASSIVE.toString())
                    .and(queryByPreference(Lists.newArrayList(aoId)).get())
                    .get();
            return resultNotEmpty(query);
        }
        return false;
    }

    @Override
    public ApplicationSearchResultDTO findAllQueried(String term, ApplicationQueryParameters applicationQueryParameters) {
        DBObject query = buildQuery(term, applicationQueryParameters);
        return searchApplications(query, applicationQueryParameters);
    }

    @Override
    public List<Map<String, Object>> findAllQueriedFull(String term, ApplicationQueryParameters applicationQueryParameters) {
        LOG.debug("findFullApplications, build query: {}", System.currentTimeMillis());
        DBObject query = buildQuery(term, applicationQueryParameters);
        LOG.debug("findFullApplications, query built: {}", System.currentTimeMillis());
        return searchApplicationsFull(query, applicationQueryParameters);
    }

    private DBObject buildQuery(String term, ApplicationQueryParameters applicationQueryParameters) {

        LOG.debug("Entering findAllQueried");
        DBObject[] filters = buildQueryFilter(applicationQueryParameters);
        StringTokenizer st = new StringTokenizer(term, " ");
        ArrayList<DBObject> queries = new ArrayList<DBObject>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            LOG.debug("processing token: {}", token);
            if (OID_PATTERN.matcher(token).matches()) {
                if (token.indexOf('.') > -1) { // Long form
                    if (token.startsWith(applicationOidPrefix)) {
                        queries.add(QueryBuilder.start(FIELD_APPLICATION_OID).is(token).get());
                    } else if (token.startsWith(userOidPrefix)) {
                        queries.add(QueryBuilder.start(FIELD_PERSON_OID).is(token).get());
                    } else {
                        queries = addDobOrNameQuery(queries, token);
                    }
                } else { // Short form
                    queries.add(
                            QueryBuilder.start().or(
                                    QueryBuilder.start(FIELD_APPLICATION_OID).is(applicationOidPrefix + "." + token).get(),
                                    QueryBuilder.start(FIELD_PERSON_OID).is(userOidPrefix + "." + token).get()
                            ).get()
                    );
                }
            } else if (HETU_PATTERN.matcher(token).matches()) {
                String encryptedSsn = shaEncrypter.encrypt(token.toUpperCase());
                queries.add(
                        QueryBuilder.start(FIELD_SSN_DIGEST).is(encryptedSsn).get()
                );
            } else { // Name or date of birth
                queries = addDobOrNameQuery(queries, token);
            }
        }

        QueryBuilder baseQuery = queries.size() > 0 ? QueryBuilder.start().and(queries.toArray(new DBObject[queries.size()])) : QueryBuilder.start();
        DBObject query = newQueryBuilderWithFilters(filters, baseQuery);
        LOG.debug("Constructed query: {}", query.toString());
        return query;
    }

    private ArrayList<DBObject> addDobOrNameQuery(ArrayList<DBObject> queries, String token) {
        String possibleDob = token.replace(".", "");
        Date dob = tryDate(HETU_DATE, possibleDob);
        if (dob == null) {
            dob = tryDate(LONG_HETU_DATE, possibleDob);
        }
        if (dob != null) {
            queries.add(
                    QueryBuilder.start(FIELD_DATE_OF_BIRTH).is(FORM_DATE.format(dob)).get()
            );
        } else {
            queries.add(
                    QueryBuilder.start(FIELD_SEARCH_NAMES).regex(Pattern.compile(REGEX_LINE_BEGIN + token.toLowerCase())).get()
            );
        }
        return queries;
    }

    private Date tryDate(DateFormat df, String str) {
        Date date = null;
        try {
            date = df.parse(str);
        } catch (ParseException pe) {
            // NOP
        }
        return date;
    }

    private QueryBuilder queryByPreference(final List<String> aoIds) {
        DBObject[] queries = new DBObject[5];
        for (int i = 0; i < queries.length; i++) {
            queries[i] = QueryBuilder.start(String.format(FIELD_AO_T, i+1)).in(aoIds).get();
        }
        return QueryBuilder.start().or(queries);
    }

    private List<Application> findApplications(DBObject dbObject) {
        final DBCursor dbCursor = getCollection().find(dbObject);
        return Lists.newArrayList(Iterables.transform(dbCursor, fromDBObject));
    }

    private ApplicationSearchResultDTO searchApplications(DBObject query, ApplicationQueryParameters params) {
        LOG.debug("Query: {}", query);
        int start = params.getStart();
        int rows = params.getRows();
        String orderBy = params.getOrderBy();
        int orderDir = params.getOrderDir();
        DBObject keys = new BasicDBObject();
        keys.put("oid", 1);
        keys.put("state", 1);
        keys.put("personOid", 1);
        keys.put("answers.henkilotiedot.Henkilotunnus", 1);
        keys.put("answers.henkilotiedot.Etunimet", 1);
        keys.put("answers.henkilotiedot.Sukunimi", 1);
        keys.put("answers.henkilotiedot.syntymaaika", 1);
        final DBCursor dbCursor = getCollection().find(query, keys).sort(new BasicDBObject(orderBy, orderDir))
                .skip(start).limit(rows).setReadPreference(ReadPreference.secondaryPreferred());
        LOG.debug("Matches: {}", dbCursor.count());
        return new ApplicationSearchResultDTO(dbCursor.count(), Lists.newArrayList(Iterables.transform(dbCursor, dbObjectToSearchResultItem)));
    }

    private List<Map<String, Object>> searchApplicationsFull(DBObject query, ApplicationQueryParameters params) {
        int start = params.getStart();
        int rows = params.getRows();
        String orderBy = params.getOrderBy();
        int orderDir = params.getOrderDir();
        DBObject keys = new BasicDBObject();
        keys.put("type", 1);
        keys.put("applicationSystemId", 1);
        keys.put("answers", 1);
        keys.put("oid", 1);
        keys.put("state", 1);
        keys.put("personOid", 1);
        keys.put("studentOid", 1);
        keys.put("received", 1);
        LOG.debug("findFullApplications, getting cursor: {}", System.currentTimeMillis());
        final DBCursor dbCursor = getCollection().find(query, keys).sort(new BasicDBObject(orderBy, orderDir))
                .skip(start).limit(rows).setReadPreference(ReadPreference.secondaryPreferred());
        LOG.debug("findFullApplications, found {} matches: {}", dbCursor.count(), System.currentTimeMillis());
        List<Map<String, Object>> apps = new ArrayList<Map<String, Object>>(dbCursor.count());
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            apps.add(dbObjectToMapFunction.apply(obj));
        }
        LOG.debug("findFullApplications, all serialized: {}", System.currentTimeMillis());
        return apps;
    }

    private DBObject[] buildQueryFilter(final ApplicationQueryParameters applicationQueryParameters) {
        ArrayList<DBObject> filters = new ArrayList<DBObject>();
        DBObject stateQuery = null;


        // Koskee yksittäistä hakutoivetta
        String lopOid = applicationQueryParameters.getLopOid();
        String preference = applicationQueryParameters.getAoId();
        boolean discretionaryOnly = applicationQueryParameters.isDiscretionaryOnly();
        String aoOid = applicationQueryParameters.getAoOid();

        ArrayList<DBObject> preferenceQueries = new ArrayList<DBObject>();
        for (int i = 1; i <= 5; i++) {
            ArrayList<DBObject> preferenceQuery = new ArrayList<DBObject>(5);
            if (isNotBlank(lopOid)) {
                preferenceQuery.add(
                        QueryBuilder.start(String.format(FIELD_LOP_PARENTS_T, i)).regex(Pattern.compile(lopOid)).get());
            }
            if (isNotBlank(preference)) {
                preferenceQuery.add(
                        QueryBuilder.start(String.format(FIELD_AO_KOULUTUS_ID_T, i)).is(preference).get());
            }
            if (discretionaryOnly) {
                preferenceQuery.add(
                        QueryBuilder.start(String.format(FIELD_DISCRETIONARY_T, i)).is("true").get());
            }
            if (isNotBlank(aoOid)) {
                preferenceQuery.add(
                        QueryBuilder.start(String.format(FIELD_AO_T, i)).is(aoOid).get());
            }
            if (!preferenceQuery.isEmpty()) {
                preferenceQueries.add(QueryBuilder.start().and(
                        preferenceQuery.toArray(new DBObject[preferenceQuery.size()])).get());
            }
        }
        if (!preferenceQueries.isEmpty()) {
            filters.add(QueryBuilder.start().or(
                preferenceQueries.toArray(new DBObject[preferenceQueries.size()])).get());
        }

        // Koskee koko hakemusta
        List<String> state = applicationQueryParameters.getState();
        if (state != null && !state.isEmpty()) {
            if (state.size() == 1 && "NOT_IDENTIFIED".equals(state.get(0))) {
                stateQuery = QueryBuilder.start(FIELD_STUDENT_OID).exists(false).get();
            } else if (state.size() == 1 && "NO_SSN".equals(state.get(0))) {
                stateQuery = QueryBuilder.start(FIELD_SSN).exists(false).get();
            } else {
                stateQuery = QueryBuilder.start(FIELD_APPLICATION_STATE).in(state).get();
            }
        }

        if (stateQuery != null) {
            filters.add(stateQuery);
        }

        List<String> asIds = applicationQueryParameters.getAsIds();
        if (!asIds.isEmpty()) {
            filters.add(QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).in(asIds).get());
        }


        String sendingSchool = applicationQueryParameters.getSendingSchool();
        if (!isEmpty(sendingSchool)) {
            filters.add(QueryBuilder.start(FIELD_SENDING_SCHOOL).is(sendingSchool).get());
        }

        String sendingClass = applicationQueryParameters.getSendingClass();
        if (!isEmpty(sendingClass)) {
            filters.add(QueryBuilder.start().or(
                    QueryBuilder.start(FIELD_SENDING_CLASS).is(sendingClass.toUpperCase()).get(),
                    QueryBuilder.start(FIELD_CLASS_LEVEL).is(sendingClass.toUpperCase()).get()
            ).get());
        }

        Date updatedAfter = applicationQueryParameters.getUpdatedAfter();
        if (updatedAfter != null) {
            filters.add(
                    QueryBuilder.start().or(
                            QueryBuilder.start(FIELD_RECEIVED).greaterThanEquals(updatedAfter.getTime()).get(),
                            QueryBuilder.start(FIELD_UPDATED).greaterThanEquals(updatedAfter.getTime()).get()
                    ).get()
            );
        }

        filters.add(newOIdExistDBObject());

        return filters.toArray(new DBObject[filters.size()]);
    }

    private ArrayList<DBObject> filterByOrganization() {

        List<String> henkOrgs = authenticationService.getOrganisaatioHenkilo();
        List<String> orgs = hakuPermissionService.userCanReadApplications(henkOrgs);

        LOG.debug("OrganisaatioHenkilo.canRead().count() == {} ", orgs.size());
        ArrayList<DBObject> queries = new ArrayList<DBObject>(orgs.size());

        for (String org : orgs) {
            Pattern orgPattern = Pattern.compile(org);
            DBObject[] lopQueries = new DBObject[6];
            lopQueries[0] = QueryBuilder.start(String.format(FIELD_LOP_PARENTS_T, 1)).is(null).get(); // Empty applications
            for (int i = 1; i <= 5; i++) {
                lopQueries[i] = QueryBuilder.start(String.format(FIELD_LOP_PARENTS_T, i)).regex(orgPattern).get();
            }
            queries.add(QueryBuilder.start().or(lopQueries).get());
        }

        List<String> opoOrgs = hakuPermissionService.userHasOpoRole(henkOrgs);
        LOG.debug("User has OPO roles: [{}]", join(opoOrgs, ","));
        if (!opoOrgs.isEmpty()) {
            for (String opoOrg : opoOrgs) {
                Pattern opoOrgPattern = Pattern.compile(opoOrg);
                queries.add(QueryBuilder.start(FIELD_SENDING_SCHOOL_PARENTS).regex(opoOrgPattern).get());
            }
        }

        LOG.debug("queries: {}", queries.size());

        return queries;
    }

    private DBObject newQueryBuilderWithFilters(final DBObject[] filters, final QueryBuilder baseQuery) {
        DBObject query;
        ArrayList<DBObject> orgFilter = filterByOrganization();

        LOG.debug("Filters: {}", filters.length);

        if (orgFilter.isEmpty()) {
            query = QueryBuilder.start("_id").exists(false).get();
        } else {
            if (filters.length > 0) {
                query = QueryBuilder.start()
                        .and(baseQuery.get(),
                                QueryBuilder.start().and(filters).get(),
                                QueryBuilder.start().or(orgFilter.toArray(new DBObject[orgFilter.size()])).get())
                        .get();
            } else {
                query = QueryBuilder.start()
                        .and(baseQuery.get(),
                                QueryBuilder.start().or(orgFilter.toArray(new DBObject[orgFilter.size()])).get())
                        .get();
            }
        }

        return query;
    }

    private DBObject newOIdExistDBObject() {
        return QueryBuilder.start(FIELD_APPLICATION_OID).exists(true).get();
    }

    @Override
    public void updateKeyValue(String oid, String key, String value) {
        DBObject query = new BasicDBObject(FIELD_APPLICATION_OID, oid);
        DBObject update = new BasicDBObject("$set", new BasicDBObject(key, value));
        getCollection().findAndModify(query, update);

        DBObject updateTimestamp = new BasicDBObject("$set", new BasicDBObject(FIELD_UPDATED, new Date()));
        getCollection().findAndModify(query, updateTimestamp);
    }

    @Override
    public Application getNextWithoutStudentOid() {
        DBObject query = new BasicDBObject();
        query.put(FIELD_APPLICATION_OID, new BasicDBObject(EXISTS, true));
        query.put(FIELD_PERSON_OID, new BasicDBObject(EXISTS, true));
        query.put(FIELD_STUDENT_OID, new BasicDBObject(EXISTS, false));
        query.put(FIELD_APPLICATION_STATE, Application.State.ACTIVE.toString());
        query.put(FIELD_STUDENT_IDENTIFICATION_DONE, Boolean.FALSE.toString());

        DBObject sortBy = new BasicDBObject(FIELD_LAST_AUTOMATED_PROCESSING_TIME, 1);

        DBCursor cursor = getCollection().find(query).sort(sortBy).limit(1);
        if (ensureIndex) {
            DBObject hint = new BasicDBObject();
            hint.put(FIELD_APPLICATION_STATE, 1);
            hint.put(FIELD_STUDENT_IDENTIFICATION_DONE, 1);
            hint.put(FIELD_LAST_AUTOMATED_PROCESSING_TIME, 1);
            cursor.hint(hint);
        }
        if (!cursor.hasNext()) {
            return null;
        }
        return fromDBObject.apply(cursor.next());
    }

    @Override
    public Application getNextSubmittedApplication() {
        DBObject query = new BasicDBObject();

        query.put(FIELD_PERSON_OID, new BasicDBObject("$exists", false));
        query.put(FIELD_APPLICATION_OID, new BasicDBObject("$exists", true));
        query.put(FIELD_APPLICATION_STATE, Application.State.SUBMITTED.toString());

        DBObject sortBy = new BasicDBObject(FIELD_LAST_AUTOMATED_PROCESSING_TIME, 1);


        DBCursor cursor = getCollection().find(query).sort(sortBy).limit(1);
        if (ensureIndex) {
            DBObject hint = new BasicDBObject(FIELD_APPLICATION_STATE, 1);
            hint.put(FIELD_LAST_AUTOMATED_PROCESSING_TIME, 1);
            cursor.hint(hint);
        }
        if (!cursor.hasNext()) {
            return null;
        }
        return fromDBObject.apply(cursor.next());
    }

    @Override
    public Application getNextRedo() {

        DBObject query = QueryBuilder.start(FIELD_REDO_POSTPROCESS).in(Lists.newArrayList("FULL", "NOMAIL")).get();
        DBObject sortBy = new BasicDBObject(FIELD_LAST_AUTOMATED_PROCESSING_TIME, 1);
        DBCursor cursor = getCollection().find(query).sort(sortBy).limit(1);
        if (!cursor.hasNext()) {
            return null;
        }
        return fromDBObject.apply(cursor.next());
    }

    private boolean resultNotEmpty(final DBObject query) {
        return getCollection().find(query).limit(1).size() > 0;
    }

    @PostConstruct
    public void ensureIndexes() {

        if (!ensureIndex) {
            return;
        }

        createIndex(INDEX_APPLICATION_OID, false, FIELD_APPLICATION_OID);
        createIndex(INDEX_SSN, false, FIELD_SSN);
        createIndex(INDEX_SSN_DIGEST, false, FIELD_SSN_DIGEST);
        createIndex(INDEX_DATE_OF_BIRTH, false, FIELD_DATE_OF_BIRTH);
        createIndex(INDEX_PERSON_OID, false, FIELD_PERSON_OID);
        createIndex(INDEX_STUDENT_OID, false, FIELD_STUDENT_OID);
        createIndex(INDEX_STATE, false, FIELD_APPLICATION_STATE, FIELD_LAST_AUTOMATED_PROCESSING_TIME);
        createIndex(INDEX_STUDENT_IDENTIFICATION_DONE, true, FIELD_APPLICATION_STATE, FIELD_STUDENT_IDENTIFICATION_DONE, FIELD_LAST_AUTOMATED_PROCESSING_TIME);
        createIndex(INDEX_SENDING_SCHOOL, true, FIELD_SENDING_SCHOOL, FIELD_SENDING_CLASS);
        createIndex(INDEX_SENDING_CLASS, true, FIELD_SENDING_CLASS);
        createIndex(INDEX_SEARCH_NAMES, false, FIELD_SEARCH_NAMES);
        createIndex(INDEX_REDO_POSTPROCESS, true, FIELD_REDO_POSTPROCESS);

        // Preference Indexes
        for (int i = 1; i <= 5; i++) {
            createPreferenceIndexes("preference"+i, i>1,
                    String.format(FIELD_LOP_T, i),
                    String.format(FIELD_DISCRETIONARY_T, i),
                    String.format(FIELD_AO_T, i),
                    String.format(FIELD_AO_KOULUTUS_ID_T, i));

        }
    }

    private void createPreferenceIndexes(String preference, Boolean sparsePossible, String lopField, String discretionaryField, String fieldAo, String fieldAoIdentifier) {
        createIndex("index_ " + preference + "_lop", sparsePossible.booleanValue(), lopField);
        createIndex("index_ " + preference + "_discretionary", true, discretionaryField);
        createIndex("index_ " + preference + "_ao", sparsePossible.booleanValue(), fieldAo);
        createIndex("index_ " + preference + "_ao_identifier", sparsePossible.booleanValue(), fieldAoIdentifier);
    }

    private void createIndex(String name, Boolean isSparse, String... fields) {
        final DBObject options = new BasicDBObject(OPTION_NAME, name);
        options.put(OPTION_SPARSE, isSparse.booleanValue());

        final DBObject index = new BasicDBObject();
        for (String field : fields) {
            index.put(field, 1);
        }
        getCollection().ensureIndex(index, options);
    }

    @Override
    public void update(Application o, Application n) {
        n.setUpdated(new Date());
        super.update(o, n);
    }

    @Override
    public void save(Application application) {
        application.setUpdated(new Date());
        super.save(application);
    }
}
