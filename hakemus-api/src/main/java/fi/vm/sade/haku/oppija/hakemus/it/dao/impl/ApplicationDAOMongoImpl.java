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
import com.google.common.collect.Lists;
import com.mongodb.*;
import fi.vm.sade.haku.oppija.common.dao.AbstractDAOMongoImpl;
import fi.vm.sade.haku.oppija.hakemus.converter.*;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Application.PostProcessingState;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationFilterParameters;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.service.EncrypterService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
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

import static com.mongodb.QueryOperators.IN;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.*;
import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoConstants.*;


/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl<Application> implements ApplicationDAO {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDAOMongoImpl.class);

    private static final String REGEX_LINE_BEGIN = "^";

    //Operations
    private static final String OPERATION_SET = "$set";

    private static final Pattern OID_PATTERN = Pattern.compile("((^([0-9]{1,4}\\.){5})|(^))[0-9]{11}$");
    private static final Pattern HETU_PATTERN = Pattern.compile("^[0-3][0-9][0-1][0-9][0-9][0-9][-+Aa][0-9]{3}[0-9a-zA-Z]");

    private final EncrypterService shaEncrypter;
    private final DBObjectToSearchResultItem dbObjectToSearchResultItem;
    private final DBObjectToMapFunction dbObjectToMapFunction;

    @Value("${mongodb.ensureIndex:true}")
    private boolean ensureIndex;

    @Value("${mongodb.enableSearchOnSecondary:true}")
    private boolean enableSearchOnSecondary;
    @Value("${application.oid.prefix}")
    private String applicationOidPrefix;
    @Value("${user.oid.prefix}")
    private String userOidPrefix;
    @Value("${root.organisaatio.oid}")
    private String rooOrganizationOid;


    @Autowired
    public ApplicationDAOMongoImpl(DBObjectToApplicationFunction dbObjectToHakemusConverter,
                                   ApplicationToDBObjectFunction hakemusToBasicDBObjectConverter,
                                   DBObjectToMapFunction dbObjectToMapFunction,
                                   @Qualifier("shaEncrypter") EncrypterService shaEncrypter,
                                   DBObjectToSearchResultItem dbObjectToSearchResultItem) {
        super(dbObjectToHakemusConverter, hakemusToBasicDBObjectConverter);
        this.shaEncrypter = shaEncrypter;
        this.dbObjectToSearchResultItem = dbObjectToSearchResultItem;
        this.dbObjectToMapFunction = dbObjectToMapFunction;
    }

    @Override
    protected String getCollectionName() {
        return "application";
    }

    @Override
    public List<ApplicationAdditionalDataDTO> findApplicationAdditionalData(final String applicationSystemId,
                                                                            final String aoId,
                                                                            final ApplicationFilterParameters filterParameters) {
        final DBObject orgFilter = filterByOrganization(filterParameters);
        final DBObject query = QueryBuilder.start().and(
                new BasicDBObject(META_FIELD_AO, aoId),
                new BasicDBObject(FIELD_APPLICATION_SYSTEM_ID, applicationSystemId),
                QueryBuilder.start(FIELD_APPLICATION_STATE).in(
                        Lists.newArrayList(
                                Application.State.ACTIVE.toString(),
                                Application.State.INCOMPLETE.toString()))
                        .get(),
                orgFilter).get();

        final DBObject keys = generateKeysDBObject(DBObjectToAdditionalDataDTO.KEYS);

        SearchResults<ApplicationAdditionalDataDTO> results = searchListing(query, keys, null, 0, 0, new DBObjectToAdditionalDataDTO(), false);
        return results.searchResultsList;
    }

    @Override
    public boolean checkIfExistsBySocialSecurityNumber(String asId, String ssn) {
        if (!Strings.isNullOrEmpty(ssn)) {
            String encryptedSsn = shaEncrypter.encrypt(ssn.toUpperCase());
            final DBObject query = QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).is(asId)
                    .and("answers.henkilotiedot." + SocialSecurityNumber.HENKILOTUNNUS_HASH).is(encryptedSsn)
                    .and(FIELD_APPLICATION_STATE).notEquals(Application.State.PASSIVE.toString())
                    .get();
            return resultNotEmpty(query, INDEX_SSN_DIGEST);
        }
        return false;
    }

    @Override
    public boolean checkIfExistsBySocialSecurityNumberAndAo(final ApplicationFilterParameters filterParameters,
                                                            final String asId, final String ssn, final String aoId) {
        if (!Strings.isNullOrEmpty(ssn)) {
            String encryptedSsn = shaEncrypter.encrypt(ssn.toUpperCase());
            DBObject query = QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).is(asId)
                    .and("answers.henkilotiedot." + SocialSecurityNumber.HENKILOTUNNUS_HASH).is(encryptedSsn)
                    .and(FIELD_APPLICATION_STATE).notEquals(Application.State.PASSIVE.toString())
                    .and(META_FIELD_AO).is(aoId)
                    .get();
            return resultNotEmpty(query, INDEX_SSN_DIGEST);
        }
        return false;
    }

    private void createIndexForSSNCheck() {
        ensureIndex(INDEX_SSN_DIGEST, FIELD_APPLICATION_SYSTEM_ID, FIELD_SSN_DIGEST, META_FIELD_AO);
    }

    @Override
    public ApplicationSearchResultDTO findAllQueried(ApplicationQueryParameters queryParameters,
                                                     ApplicationFilterParameters filterParameters) {
        final DBObject query = buildQuery(queryParameters, filterParameters);
        final DBObject keys = generateKeysDBObject(DBObjectToSearchResultItem.KEYS);
        final DBObject sortBy = queryParameters.getOrderBy() == null ? null : new BasicDBObject(queryParameters.getOrderBy(), queryParameters.getOrderDir());
        final SearchResults<ApplicationSearchResultItemDTO> results = searchListing(query, keys, sortBy, queryParameters.getStart(), queryParameters.getRows(),
                dbObjectToSearchResultItem, true);
        return new ApplicationSearchResultDTO(results.searchHits, results.searchResultsList);
    }

    @Override
    public List<Map<String, Object>> findAllQueriedFull(final ApplicationQueryParameters queryParameters,
                                                        final ApplicationFilterParameters filterParameters) {
        LOG.debug("findFullApplications, build query: {}", System.currentTimeMillis());
        final DBObject query = buildQuery(queryParameters, filterParameters);
        LOG.debug("findFullApplications, query built: {}", System.currentTimeMillis());
        final DBObject keys = generateKeysDBObject(DBObjectToMapFunction.KEYS);
        final DBObject sortBy = queryParameters.getOrderBy() == null ? null : new BasicDBObject(queryParameters.getOrderBy(), queryParameters.getOrderDir());
        final SearchResults<Map<String, Object>> searchResults = searchListing(query, keys, sortBy, queryParameters.getStart(), queryParameters.getRows(),
                dbObjectToMapFunction, false);
        return searchResults.searchResultsList;
    }

    private DBObject buildQuery(ApplicationQueryParameters applicationQueryParameters,
                                ApplicationFilterParameters filterParameters) {
        LOG.debug("Entering buildQuery");

        final DBObject query = combineSearchTermAndFilterQueries(
                buildQueryFilter(applicationQueryParameters, filterParameters),
                filterByOrganization(filterParameters),
                createSearchTermQuery(applicationQueryParameters));
        LOG.debug("Constructed query: {}", query.toString());
        return query;
    }

    private DBObject createSearchTermQuery(final ApplicationQueryParameters applicationQueryParameters){
        final StringTokenizer tokenizedSearchTerms = new StringTokenizer(applicationQueryParameters.getSearchTerms(), " ");
        final ArrayList<DBObject> queries = new ArrayList<>();
        while (tokenizedSearchTerms.hasMoreTokens()) {
            final String searchTerm = tokenizedSearchTerms.nextToken();
            LOG.debug("processing token: {}", searchTerm);
            if (OID_PATTERN.matcher(searchTerm).matches()) {
                if (searchTerm.indexOf('.') > -1) { // Long form
                    if (searchTerm.startsWith(applicationOidPrefix)) {
                        queries.add(new BasicDBObject(FIELD_APPLICATION_OID, searchTerm));
                    } else if (searchTerm.startsWith(userOidPrefix)) {
                        queries.add(new BasicDBObject(FIELD_PERSON_OID, searchTerm));
                    } else {
                        queries.add(createDobOrNameQuery(searchTerm));
                    }
                } else { // Short form
                    queries.add(
                            QueryBuilder.start().or(
                                    QueryBuilder.start(FIELD_APPLICATION_OID).is(applicationOidPrefix + "." + searchTerm).get(),
                                    QueryBuilder.start(FIELD_PERSON_OID).is(userOidPrefix + "." + searchTerm).get()
                            ).get()
                    );
                }
            } else if (HETU_PATTERN.matcher(searchTerm).matches()) {
                String encryptedSsn = shaEncrypter.encrypt(searchTerm.toUpperCase());
                queries.add(
                        QueryBuilder.start(FIELD_SSN_DIGEST).is(encryptedSsn).get()
                );
            } else { // Name or date of birth
                queries.add(createDobOrNameQuery(searchTerm));
            }
        }

        if (queries.size() < 1)
            return null;
        else if (queries.size() > 1)
            return QueryBuilder.start().and(queries.toArray(new DBObject[queries.size()])).get();
        return queries.get(0);
    }

    private DBObject createDobOrNameQuery(final String searchTerm) {
        final String possibleDob = searchTerm.replace(".", "");
        Date dob = tryDate(new SimpleDateFormat("ddMMyy"), possibleDob);
        if (dob == null) {
            dob = tryDate(new SimpleDateFormat("ddMMyyyy"), possibleDob);
        }
        if (dob != null) {
            return QueryBuilder.start(FIELD_DATE_OF_BIRTH).is(new SimpleDateFormat("dd.MM.yyyy").format(dob)).get();
        }
        return QueryBuilder.start(FIELD_SEARCH_NAMES).regex(Pattern.compile(REGEX_LINE_BEGIN + searchTerm.toLowerCase())).get();
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

    private <T> SearchResults<T> searchListing(final DBObject query, final DBObject keys, final DBObject sortBy, final int start, final int rows,
                                               final Function<DBObject, T> transformationFunction, final boolean doCount) {
        LOG.debug("searchListing starts Query: {} Keys: {} Skipping: {} Rows: {}", query, keys, start, rows);
        final long startTime = System.currentTimeMillis();
        final DBCursor dbCursor = getCollection().find(query, keys);
        if (null != sortBy)
            dbCursor.sort(sortBy);
        if (start > 0)
            dbCursor.skip(start);
        if (rows > 0)
            dbCursor.limit(rows);
        if (enableSearchOnSecondary)
            dbCursor.setReadPreference(ReadPreference.secondaryPreferred());
        int searchHits = -1;
        // TODO: Add hint

        if (ensureIndex) {
            final String hint = addIndexHint(query);
            if (null == hint)
                dbCursor.hint(hint);
        }

        try {
            // Trying to avoid needless full table scans caused by data structuring
            if (doCount)
                searchHits = dbCursor.count();
            // Guessing for sizes
            final int listSize = doCount ? searchHits : rows > 0 ? rows : 1000;
            final List<T> results = new ArrayList<T>(listSize);
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                results.add(transformationFunction.apply(obj));
            }

            if (!doCount)
                searchHits = results.size();

            LOG.debug("searchListing ends, took {} ms. Found matches: {}, returning: {}, initial set size: {}, did count: {}",
                    (System.currentTimeMillis() - startTime), searchHits, results.size(), listSize, doCount);
            return new SearchResults<T>(searchHits, results);
        } catch (MongoException mongoException) {
            LOG.error("Got error {} with query: {} using hint: {}", mongoException.getMessage(), query, null);
            throw mongoException;
        }
    }

    private String addIndexHint(final DBObject query) {

        final String queryString = query.toString();

        boolean hasApplicationState = queryString.contains(FIELD_APPLICATION_STATE);
        boolean hasApplicationSystemId = queryString.contains(FIELD_APPLICATION_SYSTEM_ID);
        boolean hasAo = queryString.contains(META_FIELD_AO);

        if (hasAo) {
            if (hasApplicationState) {
                if (hasApplicationSystemId)
                    return LogAndReturnHint(queryString, INDEX_STATE_ASID_AO_OID);
                else
                    return LogAndReturnHint(queryString, INDEX_STATE_AO_OID);
            } else {
                if (hasApplicationSystemId)
                    return LogAndReturnHint(queryString, INDEX_ASID_AO_OID);
                else
                    return LogAndReturnHint(queryString, INDEX_AO_OID);
            }
        }

        boolean hasAllOrgs = queryString.contains(META_ALL_ORGANIZATIONS)
                && !queryString.contains(META_FIELD_OPO_ALLOWED)
                && !queryString.contains(FIELD_SSN);

        if (hasAllOrgs) {
            if (hasApplicationState) {
                if (hasApplicationSystemId)
                    return LogAndReturnHint(queryString, INDEX_STATE_ASID_ORG_OID);
                else
                    return LogAndReturnHint(queryString, INDEX_STATE_ORG_OID);

            } else {
                if (hasApplicationSystemId)
                    return LogAndReturnHint(queryString, INDEX_ASID_ORG_OID);
                else
                    return LogAndReturnHint(queryString, INDEX_ORG_OID);
            }
        }
        if (hasApplicationSystemId) {
            if (hasApplicationState)
                return LogAndReturnHint(queryString, INDEX_STATE_ASID_FN);
            else
                return LogAndReturnHint(queryString, INDEX_APPLICATION_SYSTEM_ID);
        }
        if (hasApplicationState)
            return LogAndReturnHint(queryString, INDEX_STATE_FN);
        return LogAndReturnHint(queryString, null);
    }

    private String LogAndReturnHint(final String query, final String index) {
        LOG.info("Chose: {} for query: {}", index, query);
        return index;
    }

    private DBObject[] buildQueryFilter(final ApplicationQueryParameters applicationQueryParameters,
                                        final ApplicationFilterParameters filterParameters) {
        final ArrayList<DBObject> filters = new ArrayList<>();
        final ArrayList<DBObject> preferenceQueries = createPreferenceFilters(applicationQueryParameters, filterParameters);

        if (!preferenceQueries.isEmpty()) {
            if (preferenceQueries.size() == 1) {
                filters.add(preferenceQueries.get(0));
            } else {
                filters.add(QueryBuilder.start().or(
                        preferenceQueries.toArray(new DBObject[preferenceQueries.size()])).get());
            }
        }

        final Boolean preferenceChecked = applicationQueryParameters.getPreferenceChecked();
        if (preferenceChecked != null) {
            final String aoOid = applicationQueryParameters.getAoOid();
            if (isNotBlank(aoOid)) {
                filters.add(
                        QueryBuilder.start("preferencesChecked").elemMatch(
                                QueryBuilder.start().and(
                                        new BasicDBObject("preferenceAoOid", aoOid),
                                        new BasicDBObject("checked", preferenceChecked)
                                ).get()
                        ).get()
                );
            } else {
                filters.add(
                        QueryBuilder.start().and(
                                QueryBuilder.start("preferencesChecked").not().elemMatch(
                                        new BasicDBObject("checked", !preferenceChecked)).get(),
                                QueryBuilder.start("preferencesChecked").exists(true).get()
                        ).get()
                );
            }
        }

        // Koskee koko hakemusta
        final List<String> states = applicationQueryParameters.getState();
        if (states != null && !states.isEmpty()) {
            if (states.size() == 1) {
                if ("NOT_IDENTIFIED".equals(states.get(0))) {
                    filters.add(QueryBuilder.start(FIELD_STUDENT_OID).is(null).get());
                } else if ("NO_SSN".equals(states.get(0))) {
                    filters.add(QueryBuilder.start(FIELD_SSN).is(null).get());
                } else if ("POSTPROCESS_FAILED".equals(states.get(0))) {
                    filters.add(QueryBuilder.start(FIELD_REDO_POSTPROCESS).is(PostProcessingState.FAILED.toString()).get());
                } else {
                    filters.add(QueryBuilder.start(FIELD_APPLICATION_STATE).is(Application.State.valueOf(states.get(0)).toString()).get());
                }
            }else {
                filters.add(QueryBuilder.start(FIELD_APPLICATION_STATE).in(states).get());
            }
        }

        final List<String> asIds = applicationQueryParameters.getAsIds();
        if (!asIds.isEmpty()) {
            filters.add(QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).in(asIds).get());
        }

        final String sendingSchool = applicationQueryParameters.getSendingSchool();
        if (!isEmpty(sendingSchool)) {
            filters.add(QueryBuilder.start(FIELD_SENDING_SCHOOL).is(sendingSchool).get());
        }

        final String sendingClass = applicationQueryParameters.getSendingClass();
        if (!isEmpty(sendingClass)) {
            filters.add(QueryBuilder.start().or(
                    QueryBuilder.start(FIELD_SENDING_CLASS).is(sendingClass.toUpperCase()).get(),
                    QueryBuilder.start(FIELD_CLASS_LEVEL).is(sendingClass.toUpperCase()).get()
            ).get());
        }

        final Date updatedAfter = applicationQueryParameters.getUpdatedAfter();
        if (updatedAfter != null) {
            filters.add(
                    QueryBuilder.start().or(
                            QueryBuilder.start(FIELD_RECEIVED).greaterThanEquals(updatedAfter.getTime()).get(),
                            QueryBuilder.start(FIELD_UPDATED).greaterThanEquals(updatedAfter.getTime()).get()
                    ).get()
            );
        }

        final String kohdejoukko = filterParameters.getKohdejoukko();
        final String baseEducation = applicationQueryParameters.getBaseEducation();
        if (isNotBlank(kohdejoukko) && isNotBlank(baseEducation)) {
            if (OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(kohdejoukko)) {
                filters.add(
                        QueryBuilder.start(format(FIELD_HIGHER_ED_BASE_ED_T, baseEducation))
                                .is(Boolean.TRUE.toString()).get()
                );
            }
        }

        return filters.toArray(new DBObject[filters.size()]);
    }

    private ArrayList<DBObject> createPreferenceFilters(final ApplicationQueryParameters applicationQueryParameters, final ApplicationFilterParameters filterParameters){
        // Koskee yksittäistä hakutoivetta
        final String aoOid = applicationQueryParameters.getAoOid();
        final String lopOid = applicationQueryParameters.getLopOid();
        final String preference = applicationQueryParameters.getAoId();
        final String groupOid = applicationQueryParameters.getGroupOid();
        boolean discretionaryOnly = applicationQueryParameters.isDiscretionaryOnly();
        boolean primaryPreferenceOnly = applicationQueryParameters.isPrimaryPreferenceOnly();

        // FIXME A dirty Quickfix
        if (isBlank(lopOid) && isBlank(preference) && isBlank(groupOid) && !discretionaryOnly)
            return quickfix(aoOid);

        int maxOptions = primaryPreferenceOnly && isBlank(groupOid)
                ? 1
                : filterParameters.getMaxApplicationOptions();

        final ArrayList<DBObject> preferenceQueries = new ArrayList<>();
                for (int i = 1; i <= maxOptions; i++) {
            ArrayList<DBObject> preferenceQuery = new ArrayList<>(filterParameters.getMaxApplicationOptions());
            if (isNotBlank(lopOid)) {
                preferenceQuery.add(
                        QueryBuilder.start(format(META_LOP_PARENTS_T, i)).in(Lists.newArrayList(lopOid)).get());
            }
            if (isNotBlank(preference)) {
                preferenceQuery.add(
                        QueryBuilder.start(format(FIELD_AO_KOULUTUS_ID_T, i)).is(preference).get());
            }
            if (discretionaryOnly) {
                preferenceQuery.add(
                        QueryBuilder.start(format(FIELD_DISCRETIONARY_T, i)).is("true").get());
            }
            if (isNotBlank(aoOid)) {
                preferenceQuery.add(
                        QueryBuilder.start(format(FIELD_AO_T, i)).is(aoOid).get());
            }
            if (isNotBlank(groupOid)) {
                if (!primaryPreferenceOnly) {
                    preferenceQuery.add(
                            QueryBuilder.start(format(FIELD_AO_GROUPS_T, i))
                                    .regex(Pattern.compile(groupOid)).get());
                } else {
                    for (int j = 1; j < i; j++) {
                        preferenceQuery.add(
                                QueryBuilder.start(format(FIELD_AO_GROUPS_T, j)).not().regex(Pattern.compile(groupOid)).get()
                        );
                    }
                    preferenceQuery.add(
                            QueryBuilder.start().and(
                                    QueryBuilder.start(format(FIELD_AO_GROUPS_T, i)).regex(Pattern.compile(groupOid)).get(),
                                    QueryBuilder.start(format(FIELD_AO_T, i)).is(aoOid).get()
                            ).get()
                    );
                }
            }

            if (!preferenceQuery.isEmpty()) {
                preferenceQueries.add(QueryBuilder.start().and(
                        preferenceQuery.toArray(new DBObject[preferenceQuery.size()])).get());
            }
        }
        return preferenceQueries;
    }

    private ArrayList<DBObject> quickfix(final String aoOid) {
        if (isNotBlank(aoOid))
            return Lists.newArrayList((DBObject) new BasicDBObject(META_FIELD_AO, aoOid));
        return new ArrayList<>(0);
    }

    private DBObject filterByOrganization(final ApplicationFilterParameters filterParameters) {
        final ArrayList<String> allowedOrganizations = new ArrayList<>();

        if (filterParameters.getOrganizationsReadble().size() > 0) {
            allowedOrganizations.addAll(filterParameters.getOrganizationsReadble());
        }

        if (filterParameters.getOrganizationsReadble().contains(rooOrganizationOid)) {
            allowedOrganizations.add(null);
        }

        final ArrayList<DBObject> queries = new ArrayList<>();
        if (allowedOrganizations.size() > 0) {
            queries.add(QueryBuilder.start(META_ALL_ORGANIZATIONS).in(allowedOrganizations).get());
        }

        if (filterParameters.getOrganizationsOpo().size() > 0) {
            queries.add(QueryBuilder.start().and(
                    QueryBuilder.start(META_SENDING_SCHOOL_PARENTS).in(filterParameters.getOrganizationsOpo()).get(),
                    QueryBuilder.start(META_FIELD_OPO_ALLOWED).is(true).get()).get());
        }

        if (OppijaConstants.HAKUTAPA_YHTEISHAKU.equals(filterParameters.getHakutapa())
                && OppijaConstants.KOHDEJOUKKO_KORKEAKOULU.equals(filterParameters.getKohdejoukko())
                && !filterParameters.getOrganizationsHetuttomienKasittely().isEmpty()) {
            queries.add(QueryBuilder.start(FIELD_SSN).is(null).get());
        }

        LOG.debug("queries: {}", queries.size());

        return QueryBuilder.start().or(queries.toArray(new DBObject[queries.size()])).get();
    }

    private DBObject combineSearchTermAndFilterQueries(final DBObject[] filters,
                                                       final DBObject orgFilter,
                                                       final DBObject searchTermQuery) {
        LOG.debug("Filters: {}", filters.length);
        if (orgFilter.keySet().isEmpty()) {
            return QueryBuilder.start("_id").is(null).get();
        }

        final ArrayList<DBObject> queries = new ArrayList<>(3+filters.length);
        // doing tricks to retain old order. Feel free to refactor later
        if (null != searchTermQuery)
            queries.add(searchTermQuery);
        if (filters.length > 0) {
            queries.addAll(Lists.newArrayList(filters));
        }
        queries.add(orgFilter);

        return QueryBuilder.start().and(queries.toArray(new DBObject[queries.size()])).get();
    }

    @Override
    public void updateKeyValue(String oid, String key, String value) {
        DBObject query = new BasicDBObject(FIELD_APPLICATION_OID, oid);
        DBObject update = new BasicDBObject("$set", new BasicDBObject(key, value).append(FIELD_UPDATED, new Date()));
        getCollection().findAndModify(query, update);
    }

    @Override
    public Application getNextWithoutStudentOid() {
        DBObject query = new BasicDBObject(FIELD_APPLICATION_STATE,
                new BasicDBObject(IN,
                        Lists.newArrayList(
                                Application.State.ACTIVE.name(),
                                Application.State.INCOMPLETE.name())));
        query.put(FIELD_STUDENT_IDENTIFICATION_DONE, false);
        return getNextForAutomatedProcessing(query, INDEX_STUDENT_IDENTIFICATION_DONE);
    }

    private void createIndexForStudentIdentificationDone() {
        ensureSparseIndex(INDEX_STUDENT_IDENTIFICATION_DONE,
                FIELD_APPLICATION_STATE,
                FIELD_STUDENT_IDENTIFICATION_DONE,
                FIELD_LAST_AUTOMATED_PROCESSING_TIME);
    }

    @Override
    public Application getNextSubmittedApplication() {
        DBObject query = new BasicDBObject(FIELD_APPLICATION_STATE, Application.State.SUBMITTED.toString());
        query.put(FIELD_REDO_POSTPROCESS,
                new BasicDBObject(IN, Lists.newArrayList(
                        null,
                        PostProcessingState.FULL.toString(),
                        PostProcessingState.NOMAIL.toString())));
        return getNextForAutomatedProcessing(query, INDEX_POSTPROCESS);
    }

    @Override
    public Application getNextRedo() {
        QueryBuilder queryBuilder = QueryBuilder.start(FIELD_REDO_POSTPROCESS).in(
                Lists.newArrayList(
                        PostProcessingState.FULL.toString(),
                        PostProcessingState.NOMAIL.toString()));
        queryBuilder.put(FIELD_APPLICATION_STATE).in(
                Lists.newArrayList(
                        Application.State.DRAFT.name(),
                        Application.State.ACTIVE.name(),
                        Application.State.INCOMPLETE.name()));
        DBObject query = queryBuilder.get();
        return getNextForAutomatedProcessing(query, INDEX_POSTPROCESS);
    }

    private void createIndexForPostprocess() {
        ensureIndex(INDEX_POSTPROCESS,
                FIELD_APPLICATION_STATE,
                FIELD_REDO_POSTPROCESS,
                FIELD_LAST_AUTOMATED_PROCESSING_TIME);
    }

    private Application getNextForAutomatedProcessing(final DBObject query, final String indexCandidate) {
        DBObject sortBy = new BasicDBObject(FIELD_LAST_AUTOMATED_PROCESSING_TIME, 1);

        DBObject key = generateKeysDBObject(FIELD_APPLICATION_OID);

        DBCursor cursor = getCollection().find(query, key).sort(sortBy).limit(1);
        String hint = null;
        if (ensureIndex) {
            hint = indexCandidate;
            cursor.hint(indexCandidate);
        }

        try {
            if (!cursor.hasNext()) {
                return null;
            }
            String applicationOid = fromDBObject.apply(cursor.next()).getOid();

            DBObject applicationOidDBObject = new BasicDBObject(FIELD_APPLICATION_OID, applicationOid);
            DBObject updateLastAutomatedProcessingTime = new BasicDBObject(OPERATION_SET,
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

    @Override
    public boolean hasApplicationsWithModelVersion(int versionLevel) {
        return 0 < buildUpgradableCursor(versionLevel).count();
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
                new BasicDBObject("$set", new BasicDBObject(FIELD_MODEL_VERSION, modelVersion)));
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
        ensureIndex(INDEX_STATE_FN, FIELD_APPLICATION_STATE, FIELD_FULL_NAME);
        ensureIndex(INDEX_APPLICATION_SYSTEM_ID, FIELD_APPLICATION_SYSTEM_ID, FIELD_FULL_NAME);
        ensureIndex(INDEX_SSN_DIGEST_SEARCH, FIELD_SSN_DIGEST);
        ensureIndex(INDEX_DATE_OF_BIRTH, FIELD_DATE_OF_BIRTH);
        ensureIndex(INDEX_PERSON_OID, FIELD_PERSON_OID);
        ensureIndex(INDEX_STUDENT_OID, FIELD_STUDENT_OID);
        ensureSparseIndex(INDEX_SENDING_SCHOOL, FIELD_SENDING_SCHOOL, FIELD_SENDING_CLASS);
        ensureSparseIndex(INDEX_SENDING_CLASS, FIELD_SENDING_CLASS);
        ensureSparseIndex(INDEX_ALL_ORGANIZAIONS, META_ALL_ORGANIZATIONS);
        ensureIndex(INDEX_SEARCH_NAMES, FIELD_SEARCH_NAMES);
        ensureIndex(INDEX_FULL_NAME, FIELD_FULL_NAME, META_ALL_ORGANIZATIONS);

        ensureSparseIndex(INDEX_ASID_SENDING_SCHOOL_AND_FULL_NAME, FIELD_APPLICATION_SYSTEM_ID, META_SENDING_SCHOOL_PARENTS, FIELD_FULL_NAME);
        ensureSparseIndex(INDEX_ASID_AND_SENDING_SCHOOL, FIELD_APPLICATION_SYSTEM_ID, META_SENDING_SCHOOL_PARENTS);
        ensureIndex(INDEX_STATE_ASID_AO_OID, FIELD_APPLICATION_STATE, FIELD_APPLICATION_SYSTEM_ID, META_FIELD_AO, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_STATE_AO_OID, FIELD_APPLICATION_STATE, META_FIELD_AO , FIELD_APPLICATION_OID);
        ensureIndex(INDEX_ASID_AO_OID, FIELD_APPLICATION_SYSTEM_ID, META_FIELD_AO, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_AO_OID, META_FIELD_AO, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_STATE_ASID_ORG_OID, FIELD_APPLICATION_STATE, FIELD_APPLICATION_SYSTEM_ID, META_ALL_ORGANIZATIONS, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_STATE_ORG_OID, FIELD_APPLICATION_STATE, META_ALL_ORGANIZATIONS, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_ASID_ORG_OID, FIELD_APPLICATION_SYSTEM_ID, META_ALL_ORGANIZATIONS, FIELD_APPLICATION_OID);
        ensureIndex(INDEX_ORG_OID, META_ALL_ORGANIZATIONS, FIELD_APPLICATION_OID);

        // System queries
        createIndexForStudentIdentificationDone();
        createIndexForPostprocess();
        createIndexForSSNCheck();
        ensureIndex(INDEX_MODEL_VERSION, FIELD_MODEL_VERSION);

        // Preference Indexes
        for (int i = 1; i <= 8; i++) {
            createPreferenceIndexes("preference" + i, i > 1,
                    format(FIELD_LOP_T, i),
                    format(FIELD_DISCRETIONARY_T, i),
                    format(FIELD_AO_T, i),
                    format(FIELD_AO_KOULUTUS_ID_T, i));

        }
        checkIndexes("after ensures");
    }

    private void createPreferenceIndexes(String preference, Boolean sparsePossible, String lopField, String discretionaryField, String fieldAo,
                                         String fieldAoIdentifier) {
        ensureIndex("index_" + preference + "_lop", sparsePossible.booleanValue(), lopField);
        ensureSparseIndex("index_" + preference + "_discretionary", discretionaryField);
        ensureIndex("index_" + preference + "_ao", sparsePossible.booleanValue(), fieldAo);
        ensureIndex("index_" + preference + "_ao_identifier", sparsePossible.booleanValue(), fieldAoIdentifier);
    }

    @Override
    public void update(Application o, Application n) {
        if (null == o.getOid()) {
            LOG.error("Not enough parameters for update. Oid: " + o.getOid() + ". Throwing exception");
            throw new MongoException("Not enough parameters for update. Oid: " + o.getOid() + " version: " + o.getVersion());
        }
        n.setUpdated(new Date());
        super.update(o, n);
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
