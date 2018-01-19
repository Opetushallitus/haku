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

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.mongodb.*;
import fi.vm.sade.haku.oppija.common.dao.AbstractDAOMongoImpl;
import fi.vm.sade.haku.oppija.hakemus.converter.*;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PaymentState;
import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationFilterParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.exception.IncoherentDataException;
import fi.vm.sade.haku.oppija.lomake.service.EncrypterService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.HakumaksuUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.mongodb.QueryOperators.IN;
import static fi.vm.sade.haku.oppija.hakemus.domain.Application.PAYMENT_DUE_DATE;
import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoConstants.*;
import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoPostProcessingQueries.*;
import static fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber.HENKILOTUNNUS;
import static fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber.HENKILOTUNNUS_HASH;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.ELEMENT_ID_PERSON_OID;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants.PHASE_PERSONAL;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl<Application> implements ApplicationDAO {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDAOMongoImpl.class);

    //Operations
    private static final String SET = "$set";

    private final DBObjectToSearchResultItem dbObjectToSearchResultItem;
    private final DBObjectToMapFunction dbObjectToMapFunction;
    private final ApplicationDAOMongoQueryBuilder applicationQueryBuilder;

    private final DBObjectToMapWithoutSensitiveFields dbObjectToMapWithoutSensitiveFields = new DBObjectToMapWithoutSensitiveFields();


    @Value("${mongodb.ensureIndex:true}")
    private boolean ensureIndex;

    @Value("${mongodb.enableSearchOnSecondary:true}")
    private boolean enableSearchOnSecondary;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ApplicationDAOMongoImpl(final DBObjectToApplicationFunction dbObjectToHakemusConverter,
                                   final ApplicationToDBObjectFunction hakemusToBasicDBObjectConverter,
                                   final DBObjectToMapFunction dbObjectToMapFunction,
                                   @Qualifier("shaEncrypter") final EncrypterService shaEncrypter,
                                   final DBObjectToSearchResultItem dbObjectToSearchResultItem,
                                   @Value("${root.organisaatio.oid}") final String rootOrganizationOid,
                                   @Value("${application.oid.prefix}") final String applicationOidPrefix,
                                   @Value("${user.oid.prefix}") final String userOidPrefix) {
        super("application", dbObjectToHakemusConverter, hakemusToBasicDBObjectConverter);
        this.dbObjectToSearchResultItem = dbObjectToSearchResultItem;
        this.dbObjectToMapFunction = dbObjectToMapFunction;
        this.applicationQueryBuilder = new ApplicationDAOMongoQueryBuilder(shaEncrypter, rootOrganizationOid, applicationOidPrefix, userOidPrefix);
    }

    public List<String> findMaksuvelvolliset(final String applicationSystemId, final String aoId) {
        Iterable<DBObject> results = getCollection().aggregate(EligibilityRules.maksuvelvollisetQuery(applicationSystemId, aoId)).results();

        return FluentIterable.from(results).transform(new Function<DBObject, String>() {
            @Nullable
            @Override
            public String apply(@Nullable DBObject input) {
                Object oid = input.get("oid");
                return oid != null ? oid.toString() : null;
            }
        }).filter(Objects::nonNull).toList();
    }

    @Override
    public List<ApplicationAdditionalDataDTO> findApplicationAdditionalData(final String applicationSystemId,
                                                                            final String aoId,
                                                                            final ApplicationFilterParameters filterParameters) {
        final DBObject query = applicationQueryBuilder.buildApplicationByApplicationOption(applicationSystemId, aoId, filterParameters);
        final DBObject keys = generateKeysDBObject(DBObjectToAdditionalDataDTO.KEYS);
        SearchResults<ApplicationAdditionalDataDTO> results = searchListing(query, keys, null, 0, 0, new DBObjectToAdditionalDataDTO(), false);
        return results.searchResultsList;
    }
    @Override
    public List<ApplicationAdditionalDataDTO> findApplicationAdditionalData(final List<String> oids,
                                                                            final ApplicationFilterParameters filterParameters) {
        final DBObject query = applicationQueryBuilder.buildApplicationByApplicationOption(oids, filterParameters);
        final DBObject keys = generateKeysDBObject(DBObjectToAdditionalDataDTO.KEYS);
        SearchResults<ApplicationAdditionalDataDTO> results = searchListing(query, keys, null, 0, 0, new DBObjectToAdditionalDataDTO(), false);
        return results.searchResultsList;
    }
    @Override
    public boolean checkIfExistsBySocialSecurityNumber(String asId, String ssn) {
        if (!Strings.isNullOrEmpty(ssn)) {
            return resultNotEmpty(applicationQueryBuilder.buildApplicationExistsForSSN(ssn, asId), INDEX_SSN_DIGEST);
        }
        return false;
    }

    @Override
    public boolean checkIfExistsByEmail(String asId, String email) {
        if (!Strings.isNullOrEmpty(email)) {
            return resultNotEmpty(applicationQueryBuilder.buildApplicationExistsForEmail(email, asId), INDEX_EMAIL);
        }
        return false;
    }

    @Override
    public boolean checkIfExistsBySocialSecurityNumberAndAo(final ApplicationFilterParameters filterParameters,
                                                            final String asId, final String ssn, final String aoId) {
        if (!Strings.isNullOrEmpty(ssn)) {
            return resultNotEmpty(applicationQueryBuilder.buildApplicationExistsForSSN(ssn, asId,aoId), INDEX_SSN_DIGEST);
        }
        return false;
    }

    @Override
    public boolean checkIfExistsByEmailAndAo(final ApplicationFilterParameters filterParameters,
                                             final String asId, final String email, final String aoId) {
        if (!Strings.isNullOrEmpty(email)) {
            return resultNotEmpty(applicationQueryBuilder.buildApplicationExistsForEmail(email, asId, aoId), INDEX_EMAIL);
        }
        return false;
    }

    private void createIndexForSSNCheck() {
        ensureIndex(INDEX_SSN_DIGEST, FIELD_APPLICATION_SYSTEM_ID, FIELD_SSN_DIGEST, META_FIELD_AO);
    }

    @Override
    public List<Map<String, Object>> findAllQueriedWithKeys(ApplicationQueryParameters queryParameters,
                                                             ApplicationFilterParameters filterParameters,
                                                             String... queryKeys) {
        final DBObject query = applicationQueryBuilder.buildFindAllQuery(queryParameters, filterParameters);
        final DBObject keys = generateKeysDBObject(queryKeys);
        final DBObject sortBy = queryParameters.getOrderBy() == null ? null : new BasicDBObject(queryParameters.getOrderBy(), queryParameters.getOrderDir());
        final SearchResults<Map<String, Object>> searchResults = searchListing(query, keys, sortBy, queryParameters.getStart(), queryParameters.getRows(),
                dbObjectToMapFunction, false);
        return searchResults.searchResultsList;

    }

    @Override
    public ApplicationSearchResultDTO findAllQueried(ApplicationQueryParameters queryParameters,
                                                     ApplicationFilterParameters filterParameters) {
        final DBObject query = applicationQueryBuilder.buildFindAllQuery(queryParameters, filterParameters);
        final DBObject keys = generateKeysDBObject(DBObjectToSearchResultItem.KEYS);
        final DBObject sortBy = queryParameters.getOrderBy() == null ? null : new BasicDBObject(queryParameters.getOrderBy(), queryParameters.getOrderDir());
        final SearchResults<ApplicationSearchResultItemDTO> results = searchListing(query, keys, sortBy, queryParameters.getStart(), queryParameters.getRows(),
                dbObjectToSearchResultItem, true);
        return new ApplicationSearchResultDTO(results.searchHits, results.searchResultsList);
    }

    /**
     * Returns application objects for queried person OIDs. Returned fields controlled by boolean flags.
     *
     * @param personOids List of person OIDs to query
     * @param allKeys If true return all keys from the object else return a fixed subset of keys (could be generalized to take a set of keys to be returned if needed, but make sure Henkilo service is happy after this)
     * @param removeSensitiveInfo If true remove sensitive (e.g. SSN) fields from the returned objects (omits SSN decryption on the lowest level)
     * @return application objects for queried person OIDs
     */
    @Override
    public List<Map<String, Object>> findApplicationsByPersonOid(Set<String> personOids, final boolean allKeys, final boolean removeSensitiveInfo) {
        DBObject query = QueryBuilder.start(ELEMENT_ID_PERSON_OID).in(personOids).get();
        DBObject keys = allKeys ?
                generateKeysDBObject(DBObjectToMapFunction.KEYS)
                : generateKeysDBObject("answers." + PHASE_PERSONAL, "oid", ELEMENT_ID_PERSON_OID, FIELD_RECEIVED, FIELD_APPLICATION_STATE);
        keys.put("_id", 0);
        SearchResults<Map<String, Object>> result = simpleSearchListing(query, keys, removeSensitiveInfo ? dbObjectToMapWithoutSensitiveFields : dbObjectToMapFunction, INDEX_PERSON_OID);
        return result.searchResultsList;
    }

    @Override
    public Set<String> findPersonOidsByApplicationSystemOids(Collection<String> applicationSystemOids, String organizationOid) {
        return findPersonOidsByOidList(FIELD_APPLICATION_SYSTEM_ID, INDEX_APPLICATION_SYSTEM_ID, applicationSystemOids, organizationOid);
    }

    @Override
    public Set<String> findPersonOidsByApplicationOptionOids(Collection<String> applicationOptionOids, String organizationOid) {
        return findPersonOidsByOidList(META_FIELD_AO, INDEX_AO_OID, applicationOptionOids, organizationOid);
    }

    private Set<String> findPersonOidsByOidList(String field, String indexHint, Collection<String> oidList, String organizationOid) {
        class DBObjectToString implements Function<DBObject, String>  {
            public String apply(DBObject dbObject) {
                @SuppressWarnings("rawtypes")
                final Map fromValue = dbObject.toMap();
                return (String) fromValue.get(FIELD_PERSON_OID);
            }
        }

        QueryBuilder queryBuilder = QueryBuilder.start(field).in(oidList)
                .and(FIELD_PERSON_OID).exists(true);

        if (!isBlank(organizationOid)) {
            queryBuilder.and(META_ALL_ORGANIZATIONS).is(organizationOid);
        }

        DBObject keys = generateKeysDBObject(FIELD_PERSON_OID);
        keys.put("_id", 0);

        SearchResults<String> result = simpleSearchListing(queryBuilder.get(), keys, new DBObjectToString(), indexHint);
        return new HashSet<>(result.searchResultsList);
    }

    @Override
    public CloseableIterator<Map<String, Object>> findAllQueriedFullStreaming(final ApplicationQueryParameters queryParameters,
                                                          final ApplicationFilterParameters filterParameters) {
        final DBObject query = applicationQueryBuilder.buildFindAllQuery(queryParameters, filterParameters);
        final DBObject keys = generateKeysDBObject(DBObjectToMapFunction.KEYS);
        final DBObject sortBy = queryParameters.getOrderBy() == null ? null : new BasicDBObject(queryParameters.getOrderBy(), queryParameters.getOrderDir());
        return searchListingStreaming(query, keys, sortBy, queryParameters.getStart(), queryParameters.getRows(),
                (dobj) -> dbObjectToMapFunction.apply(dobj));
    }

    @Override
    public List<Map<String, Object>> findAllQueriedFull(final ApplicationQueryParameters queryParameters,
                                                        final ApplicationFilterParameters filterParameters) {
        final DBObject query = applicationQueryBuilder.buildFindAllQuery(queryParameters, filterParameters);
        final DBObject keys = generateKeysDBObject(DBObjectToMapFunction.KEYS);
        final DBObject sortBy = queryParameters.getOrderBy() == null ? null : new BasicDBObject(queryParameters.getOrderBy(), queryParameters.getOrderDir());
        final SearchResults<Map<String, Object>> searchResults = searchListing(query, keys, sortBy, queryParameters.getStart(), queryParameters.getRows(),
                dbObjectToMapFunction, false);
        return searchResults.searchResultsList;
    }

    static class DBObjectToMapWithoutSensitiveFields implements Function<DBObject, Map<String, Object>>  {
        public Map<String, Object> apply(DBObject dbObject) {
            @SuppressWarnings("rawtypes")
            final Map fromValue = dbObject.toMap();
            @SuppressWarnings("unchecked")
            final Map<String, Map<String, String>> answers = (Map<String, Map<String, String>>) fromValue.get("answers");
            if (answers != null) {
                final Map<String, String> henkilotiedot = answers.get("henkilotiedot");
                if (henkilotiedot != null && henkilotiedot.containsKey(SocialSecurityNumber.HENKILOTUNNUS)) {
                    henkilotiedot.remove(HENKILOTUNNUS);
                    henkilotiedot.remove(HENKILOTUNNUS_HASH);
                }
            }
            return fromValue;
        }
    }


    private <T> CloseableIterator<T> searchListingStreaming(final DBObject query, final DBObject keys, final DBObject sortBy, final int start, final int rows,
                          final java.util.function.Function<DBObject, T> transformationFunction) {
        LOG.debug("searchListing starts Query: {} Keys: {} Skipping: {} Rows: {}", query, keys, start, rows);
        final DBCursor dbCursor = searchListingToDBCursor(query, keys, sortBy, start, rows);
        return closeableIteratorFromDBCursor(transformationFunction, dbCursor);
    }

    private <T> CloseableIterator<T> closeableIteratorFromDBCursor(java.util.function.Function<DBObject, T> transformationFunction, DBCursor dbCursor) {
        return new CloseableIterator<T>() {
            final Iterator<DBObject> iterator = dbCursor.iterator();

            public boolean hasNext() {
                return iterator.hasNext();
            }
            public T next() {
                return transformationFunction.apply(iterator.next());
            }
            public void close() throws Exception {
                dbCursor.close();
            }
        };
    }

    private <T> SearchResults<T> searchListing(final DBObject query, final DBObject keys, final DBObject sortBy, final int start, final int rows,
                                               final Function<DBObject, T> transformationFunction, final boolean doCount) {
        LOG.debug("searchListing starts Query: {} Keys: {} Skipping: {} Rows: {}", query, keys, start, rows);
        final long startTime = System.currentTimeMillis();
        final DBCursor dbCursor = searchListingToDBCursor(query, keys, sortBy, start, rows);
        int searchHits = -1;

        try {
            // Trying to avoid needless full table scans caused by data structuring
            if (doCount)
                searchHits = dbCursor.count();
            // Guessing for sizes
            final int listSize = doCount ? searchHits : rows > 0 ? rows : 1000;
            try(CloseableIterator<T> apps = closeableIteratorFromDBCursor(transformationFunction::apply, dbCursor)) {
                List<T> results = Lists.newArrayListWithExpectedSize(listSize);
                Iterators.addAll(results, apps);
                if (!doCount)
                    searchHits = results.size();
                LOG.debug("searchListing ends, took {} ms. Found matches: {}, returning: {}, initial set size: {}, did count: {}",
                        (System.currentTimeMillis() - startTime), searchHits, results.size(), listSize, doCount);
                return new SearchResults<T>(searchHits, results);
            }
        } catch (Exception mongoException) {
            LOG.error("Got error {} with query: {}", mongoException.getMessage(), dbCursor);
            throw new MongoException(mongoException.getMessage(), mongoException);
        }
    }

    private DBCursor searchListingToDBCursor(DBObject query, DBObject keys, DBObject sortBy, int start, int rows) {
        final DBCursor dbCursor = getCollection().find(query, keys);
        if (null != sortBy)
            dbCursor.sort(sortBy);
        if (start > 0)
            dbCursor.skip(start);
        if (rows > 0)
            dbCursor.limit(rows);
        if (enableSearchOnSecondary)
            dbCursor.setReadPreference(ReadPreference.secondaryPreferred());
        return dbCursor;
    }

    private <T> SearchResults<T> simpleSearchListing(final DBObject query, final  DBObject keys, final Function<DBObject, T> transformationFunction, final String indexHint) {
        final long startTime = System.currentTimeMillis();
        final DBCursor dbCursor = getCollection().find(query, keys);

        try {
            if (indexHint != null && ensureIndex) {
                dbCursor.hint(indexHint);
            }
            final ImmutableList<T> results = ImmutableList.copyOf(Iterables.transform(dbCursor, transformationFunction));
            LOG.debug("simpleSearchListing took {} ms.", System.currentTimeMillis() - startTime);
            return new SearchResults<T>(results.size(), results);
        } catch (MongoException mongoException) {
            LOG.error("Got error {} with query: {}", mongoException.getMessage(), dbCursor);
            throw mongoException;
        }
    }

    @Override
    public void updateKeyValue(final String oid, final String key, final String value) {
        final DBObject query = new BasicDBObject(FIELD_APPLICATION_OID, oid);
        final DBObject update = new BasicDBObject(SET, new BasicDBObject(key, value).append(FIELD_UPDATED, new Date()));
        getCollection().findAndModify(query, update);
    }

    @Override
    public Application getNextWithoutStudentOid() {
        return getNextForAutomatedProcessing(buildIdentificationQuery(), INDEX_STUDENT_IDENTIFICATION_DONE);
    }

    @Override
    public Application getNextSubmittedApplication() {
        return getNextForAutomatedProcessing(buildSubmittedQuery(), INDEX_POSTPROCESS);
    }

    @Override
    public Application getNextRedo() {
        return getNextForAutomatedProcessing(buildRedoQuery(), INDEX_POSTPROCESS);
    }

    @Override
    public List<String> massRedoPostProcess(List<String> applicationOids, Application.PostProcessingState state) {
        List<String> affected = new ArrayList<>(applicationOids.size());
        for (String oid : applicationOids) {
            LOG.debug("Setting redoPostProcess ({}) for application {}", state.name(), oid);
            try {
                WriteResult result = getCollection().update(
                        QueryBuilder.start().and(
                                QueryBuilder.start(FIELD_APPLICATION_OID).is(oid).get(),
                                QueryBuilder.start(FIELD_APPLICATION_STATE).in(new ArrayList<String>() {{
                                    add(Application.State.ACTIVE.name());
                                    add(Application.State.INCOMPLETE.name());
                                }}).get()
                        ).get(),
                        new BasicDBObject(SET, new BasicDBObject(FIELD_REDO_POSTPROCESS, state.name())));
                int n = result.getN();
                if (n == 1) {
                    affected.add(oid);
                } else if (n > 0) {
                    LOG.error("Multiple applications for oid {}", oid);
                    throw new IncoherentDataException(String.format("Multiple applications for oid %s", oid));
                }
            } catch (MongoException mongoException) {
                LOG.error("Error when marking application {} for redo: ", oid, mongoException);
                throw mongoException;
            }
        }
        return affected;

    }

    private Application getNextForAutomatedProcessing(final DBObject query, final String indexCandidate) {
        final DBCursor cursor = getCollection().find(query, generateKeysDBObject(FIELD_APPLICATION_OID)).sort(generateKeysDBObject(FIELD_LAST_AUTOMATED_PROCESSING_TIME)).limit(1);
        String hint = null;
        if (ensureIndex) {
            hint = indexCandidate;
            cursor.hint(indexCandidate);
        }

        try {
            if (!cursor.hasNext()) {
                return null;
            }
            final String applicationOid = fromDBObject.apply(cursor.next()).getOid();

            final DBObject applicationOidDBObject = new BasicDBObject(FIELD_APPLICATION_OID, applicationOid);
            final DBObject updateLastAutomatedProcessingTime = new BasicDBObject(SET,
                    new BasicDBObject(FIELD_LAST_AUTOMATED_PROCESSING_TIME, System.currentTimeMillis()));
            getCollection().update(applicationOidDBObject, updateLastAutomatedProcessingTime, false, false);

            return fromDBObject.apply(getCollection().findOne(new BasicDBObject(FIELD_APPLICATION_OID, applicationOid)));
        } catch (MongoException mongoException) {
            LOG.error("Got error {} with query: {} using hint: {}", mongoException.getMessage(), query, hint);
            throw mongoException;
        }
    }

    @Override
    public List<Application> getNextUpgradable(int versionLevel, int batchSize) {
        final DBCursor cursor = buildUpgradableCursor(versionLevel).limit(batchSize);
        final List<Application> applications = new ArrayList<Application>(batchSize);
        while (cursor.hasNext()) {
            applications.add(fromDBObject.apply(cursor.next()));
        }
        return applications;
    }

    public static class EligibilityRules {
        public static List<DBObject> maksuvelvollisetQuery(String applicationSystemId, String aoId) {
            return asList(
                    new BasicDBObject("$match", new BasicDBObject("applicationSystemId", applicationSystemId)
                            .append("state", new BasicDBObject(QueryOperators.IN, asList(Application.State.ACTIVE.name(), Application.State.INCOMPLETE.name())))
                            .append("preferenceEligibilities.aoId", aoId)),
                    new BasicDBObject("$project", new BasicDBObject("oid",1)
                            .append("preferenceEligibilities", new BasicDBObject("$filter",
                                    new BasicDBObject("input", "$preferenceEligibilities")
                                            .append("as","item")
                                            .append("cond", new BasicDBObject("$eq", asList("$$item.aoId", aoId)) )
                                    ))),
                    new BasicDBObject("$match", new BasicDBObject("preferenceEligibilities.maksuvelvollisuus",
                            PreferenceEligibility.Maksuvelvollisuus.REQUIRED.name()))
            );
        }
    }

    public static class PaymentDueDateRules {
        private static LocalDateTime queryTime() {
            return new LocalDateTime().minusDays(HakumaksuUtil.APPLICATION_PAYMENT_WAITING_TIME);
        }

        public static BasicDBObject mongoQuery() {
            return new BasicDBObject(
                    ImmutableMap.of(
                            "paymentDueDate", new BasicDBObject("$lt", queryTime().toDate().getTime()),
                            "state", new BasicDBObject("$ne", Application.State.PASSIVE.name()),
                            "requiredPaymentState", new BasicDBObject("$in", ImmutableList.of(PaymentState.NOT_OK.name(), PaymentState.NOTIFIED.name()))
                    )
            );
        }

        public static Boolean evaluatePaymentDueDateRules(Application application) {
            return paymentDueDate(application) && state(application) && requiredPaymentState(application);
        }

        private static Boolean paymentDueDate(Application application) {
            Date paymentDueDate = application.getPaymentDueDate();
            return paymentDueDate != null && paymentDueDate.before(queryTime().toDate());
        }

        private static Boolean state(Application application) {
            Application.State state = application.getState();
            return state != null && state != Application.State.PASSIVE;
        }

        private static Boolean requiredPaymentState(Application application) {
            PaymentState paymentState = application.getRequiredPaymentState();
            return paymentState != null
                    && ImmutableList.of(PaymentState.NOT_OK, PaymentState.NOTIFIED).contains(paymentState);
        }
    }

    @Override
    public List<Application> getNextForPaymentDueDateProcessing(int batchSize) {
        final DBObject query = PaymentDueDateRules.mongoQuery();

        DBCursor dbCursor = getCollection().find(query).limit(batchSize);
        if (ensureIndex) {
            dbCursor = dbCursor.hint(INDEX_PAYMENT_DUE_DATE);
        }

        return ImmutableList.copyOf(Iterables.transform(dbCursor, fromDBObject));
    }

    @Override
    public boolean hasApplicationsWithModelVersion(int versionLevel) {
        return 0 < buildUpgradableCursor(versionLevel).count();
    }

    @Override
    public List<Application> getApplicationsByPersonOid(List<String> personOids) {
        final DBObject query = new BasicDBObject(FIELD_PERSON_OID, new BasicDBObject("$in", personOids));
        DBObject keys = generateKeysDBObject("answers.hakutoiveet", "applicationSystemId");
        DBCursor dbCursor = getCollection().find(query, keys);
        List<Application> results = Lists.newArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            Application application = objectMapper.convertValue(obj, Application.class);
            results.add(application);
        }
        return results;
    }

    @Override
    public int removeApplicationsReceivedBeforeDate(Date after) {
        String host = mongoTemplate.getDb().getMongo().getAddress().getHost();
        if("oph-mongodb-hakemus-new.hard.ware.fi".equals(host) == false) {
            throw new RuntimeException("Tried to run cleanup on wrong database. Host: " + host);
        }

        final DBObject query = QueryBuilder.start(FIELD_RECEIVED).lessThanEquals(after.getTime()).get();
        WriteResult result = getCollection().remove(query);

        LOG.info("Removed applications: " + result.getN());
        return result.getN();
    }


    private DBCursor buildUpgradableCursor(int versionLevel) {
        DBObject query;
        if (versionLevel > 1)
            query = new BasicDBObject(FIELD_MODEL_VERSION, versionLevel);
        else {
            //legacy upgrade
            query = new BasicDBObject(FIELD_MODEL_VERSION,
                    new BasicDBObject(IN,
                            new Object[]{null, 0, 1}));
        }
        final DBCursor cursor = getCollection().find(query);
        if (ensureIndex)
            cursor.hint(INDEX_MODEL_VERSION);
        return cursor;
    }

    @Override
    public Application getApplication(final String oid, String... fields) {
        final DBObject query = new BasicDBObject(FIELD_APPLICATION_OID, oid);

        DBObject keys = generateKeysDBObject(fields);
        DBCursor cursor = getCollection().find(query, keys);
        if (ensureIndex) {
            cursor.hint(INDEX_APPLICATION_OID);
        }
        if (cursor.hasNext())
            return fromDBObject.apply(cursor.next());
        return null;
    }

    public void updateModelVersion(final Application application, int modelVersion) {
        getCollection().update(
                new BasicDBObject(FIELD_APPLICATION_OID, application.getOid()).append(FIELD_APPLICATION_VERSION, application.getVersion()),
                new BasicDBObject(SET, new BasicDBObject(FIELD_MODEL_VERSION, modelVersion)));
    }

    private boolean resultNotEmpty(final DBObject query, final String indexName) {
        try {
            DBCursor cursor = getCollection().find(query).limit(1);
            if (ensureIndex && !isEmpty(indexName))
                cursor.hint(indexName);
            return cursor.size() > 0;
        } catch (MongoException mongoException) {
            LOG.error("Got error {} with query: {} using hint: {}", mongoException.getMessage(), query, indexName);
            throw mongoException;
        }
    }

    @PostConstruct
    public void configure() {
        if (!ensureIndex) {
            return;
        }

        checkIndexes("before ensures");

        // constraint indexes
        ensureUniqueIndex(INDEX_APPLICATION_OID, FIELD_APPLICATION_OID);
        // default query indexes
        ensureIndex(INDEX_STATE_ASID_FN, FIELD_APPLICATION_STATE, FIELD_APPLICATION_SYSTEM_ID, FIELD_FULL_NAME);
        ensureIndex(INDEX_APPLICATION_SYSTEM_ID, FIELD_APPLICATION_SYSTEM_ID, FIELD_FULL_NAME);
        ensureIndex(INDEX_SSN_DIGEST_SEARCH, FIELD_SSN_DIGEST);
        ensureIndex(INDEX_DATE_OF_BIRTH, FIELD_DATE_OF_BIRTH);
        ensureIndex(INDEX_PERSON_OID, FIELD_PERSON_OID);
        ensureIndex(INDEX_EMAIL, FIELD_EMAIL);
        ensureSparseIndex(INDEX_SENDING_SCHOOL, FIELD_SENDING_SCHOOL, FIELD_SENDING_CLASS);
        ensureIndex(INDEX_SEARCH_NAMES, FIELD_SEARCH_NAMES);
        ensureIndex(INDEX_FULL_NAME, FIELD_FULL_NAME, META_ALL_ORGANIZATIONS);

        ensureSparseIndex(INDEX_ASID_SENDING_SCHOOL_AND_FULL_NAME, FIELD_APPLICATION_SYSTEM_ID, META_SENDING_SCHOOL_PARENTS, FIELD_FULL_NAME);
        ensureSparseIndex(INDEX_ASID_AND_SENDING_SCHOOL, FIELD_APPLICATION_SYSTEM_ID, META_SENDING_SCHOOL_PARENTS);
        ensureIndex(INDEX_STATE_ASID_AO_OID, FIELD_APPLICATION_STATE, FIELD_APPLICATION_SYSTEM_ID, META_FIELD_AO, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_STATE_AO_OID, FIELD_APPLICATION_STATE, META_FIELD_AO, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_ASID_AO_OID, FIELD_APPLICATION_SYSTEM_ID, META_FIELD_AO, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_AO_OID, META_FIELD_AO, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_STATE_ASID_ORG_OID, FIELD_APPLICATION_STATE, FIELD_APPLICATION_SYSTEM_ID, META_ALL_ORGANIZATIONS, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_STATE_ORG_OID, FIELD_APPLICATION_STATE, META_ALL_ORGANIZATIONS, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_ASID_ORG_OID, FIELD_APPLICATION_SYSTEM_ID, META_ALL_ORGANIZATIONS, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_ORG_OID, META_ALL_ORGANIZATIONS, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_UPDATED, FIELD_UPDATED);
        ensureIndex(INDEX_UPDATED_RECEIVED, FIELD_RECEIVED, FIELD_UPDATED);
        ensureSparseIndex(INDEX_PAYMENT_DUE_DATE, PAYMENT_DUE_DATE);

        // System queries
        ensureSparseIndex(INDEX_STUDENT_IDENTIFICATION_DONE, INDEX_STUDENT_IDENTIFICATION_DONE_FIELDS);
        ensureIndex(INDEX_POSTPROCESS, INDEX_POSTPROCESS_FIELDS);
        createIndexForSSNCheck();
        ensureIndex(INDEX_MODEL_VERSION, FIELD_MODEL_VERSION);

        // Preference Indexes
        for (int i = 1; i <= 8; i++) {
            createPreferenceIndexes("preference" + i, i > 1,
                    format(FIELD_AO_T, i),
                    format(FIELD_AO_KOULUTUS_ID_T, i));

        }
        checkIndexes("after ensures");
    }

    private void createPreferenceIndexes(String preference, boolean sparsePossible, String fieldAo, String fieldAoIdentifier) {
        ensureIndex("index_" + preference + "_ao", sparsePossible, fieldAo);
        ensureIndex("index_" + preference + "_ao_identifier", sparsePossible, fieldAoIdentifier);
    }

    @Override
    public int update(Application o, Application n) {
        if (null == o.getOid()) {
            LOG.error("Not enough parameters for update. Oid: " + o.getOid() + ". Throwing exception");
            throw new MongoException("Not enough parameters for update. Oid: " + o.getOid() + " version: " + o.getVersion());
        }
        n.setUpdated(new Date());
        return super.update(o, n);
    }

    @Override
    public void save(final Application application) {
        if (null == application || null == application.getOid()) {
            LOG.error("Missing required attributes. Save aborted and throwing exception. Application data was {}", application);
            throw new MongoException("Application is missing required attributes");
        }

        DBObject check = new BasicDBObject(FIELD_APPLICATION_OID, application.getOid());
        if (getCollection().find(check).count() > 0) {
            LOG.error("System already contains and application with oid: " + application.getOid() + ". Throwing exception. Application data was {}",
                    application);
            throw new MongoException("System Already contains and application with oid: " + application.getOid());
        }
        if (null == application.getModelVersion())
            application.setModelVersion(application.getModelVersion());
        application.setUpdated(new Date());
        application.enforceLowercaseEmail();
        super.save(application);
    }

    private class SearchResults<T> {
        private final int searchHits;
        private final List<T> searchResultsList;

        private SearchResults(int searchHits, List<T> searchResults) {
            this.searchHits = searchHits;
            this.searchResultsList = searchResults;
        }
    }
}
