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
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.ReadPreference;
import fi.vm.sade.haku.oppija.common.dao.AbstractDAOMongoImpl;
import fi.vm.sade.haku.oppija.hakemus.converter.ApplicationToDBObjectFunction;
import fi.vm.sade.haku.oppija.hakemus.converter.DBObjectToApplicationFunction;
import fi.vm.sade.haku.oppija.hakemus.converter.DBObjectToSearchResultItem;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.hakemus.service.HakuPermissionService;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.haku.oppija.lomake.service.EncrypterService;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl<Application> implements ApplicationDAO {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDAOMongoImpl.class);
    private static final String FIELD_AO_1 = "answers.hakutoiveet.preference1-Koulutus-id";
    private static final String FIELD_AO_2 = "answers.hakutoiveet.preference2-Koulutus-id";
    private static final String FIELD_AO_3 = "answers.hakutoiveet.preference3-Koulutus-id";
    private static final String FIELD_AO_4 = "answers.hakutoiveet.preference4-Koulutus-id";
    private static final String FIELD_AO_5 = "answers.hakutoiveet.preference5-Koulutus-id";
    private static final String FIELD_AO_KOULUTUS_ID_1 = "answers.hakutoiveet.preference1-Koulutus-id-aoIdentifier";
    private static final String FIELD_AO_KOULUTUS_ID_2 = "answers.hakutoiveet.preference2-Koulutus-id-aoIdentifier";
    private static final String FIELD_AO_KOULUTUS_ID_3 = "answers.hakutoiveet.preference3-Koulutus-id-aoIdentifier";
    private static final String FIELD_AO_KOULUTUS_ID_4 = "answers.hakutoiveet.preference4-Koulutus-id-aoIdentifier";
    private static final String FIELD_AO_KOULUTUS_ID_5 = "answers.hakutoiveet.preference5-Koulutus-id-aoIdentifier";
    private static final String FIELD_LOP_1 = "answers.hakutoiveet.preference1-Opetuspiste-id";
    private static final String FIELD_LOP_2 = "answers.hakutoiveet.preference2-Opetuspiste-id";
    private static final String FIELD_LOP_3 = "answers.hakutoiveet.preference3-Opetuspiste-id";
    private static final String FIELD_LOP_4 = "answers.hakutoiveet.preference4-Opetuspiste-id";
    private static final String FIELD_LOP_5 = "answers.hakutoiveet.preference5-Opetuspiste-id";
    private static final String FIELD_LOP_PARENTS_1 = "answers.hakutoiveet.preference1-Opetuspiste-id-parents";
    private static final String FIELD_LOP_PARENTS_2 = "answers.hakutoiveet.preference2-Opetuspiste-id-parents";
    private static final String FIELD_LOP_PARENTS_3 = "answers.hakutoiveet.preference3-Opetuspiste-id-parents";
    private static final String FIELD_LOP_PARENTS_4 = "answers.hakutoiveet.preference4-Opetuspiste-id-parents";
    private static final String FIELD_LOP_PARENTS_5 = "answers.hakutoiveet.preference5-Opetuspiste-id-parents";
    private static final String FIELD_DISCRETIONARY_1 = "answers.hakutoiveet.preference1-discretionary";
    private static final String FIELD_DISCRETIONARY_2 = "answers.hakutoiveet.preference2-discretionary";
    private static final String FIELD_DISCRETIONARY_3 = "answers.hakutoiveet.preference3-discretionary";
    private static final String FIELD_DISCRETIONARY_4 = "answers.hakutoiveet.preference4-discretionary";
    private static final String FIELD_DISCRETIONARY_5 = "answers.hakutoiveet.preference5-discretionary";
    private static final String FIELD_APPLICATION_OID = "oid";
    private static final String FIELD_APPLICATION_SYSTEM_ID = "applicationSystemId";
    private static final String FIELD_PERSON_OID = "personOid";
    private static final String FIELD_APPLICATION_STATE = "state";
    private static final String FIELD_LAST_AUTOMATED_PROCESSING_TIME = "lastAutomatedProcessingTime";
    private static final String FIELD_SENDING_SCHOOL = "answers.koulutustausta.lahtokoulu";
    private static final String FIELD_SENDING_CLASS = "answers.koulutustausta.lahtoluokka";
    private static final String FIELD_SSN = "answers.henkilotiedot.Henkilotunnus";
    private static final String EXISTS = "$exists";
    private static final String FIELD_STUDENT_OID = "studentOid";
    private static final String FIELD_STUDENT_IDENTIFICATION_DONE = "studentIdentificationDone";
    private final EncrypterService shaEncrypter;
    private final DBObjectToSearchResultItem dbObjectToSearchResultItem;

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
                                   @Qualifier("shaEncrypter") EncrypterService shaEncrypter,
                                   DBObjectToSearchResultItem dbObjectToSearchResultItem,
                                   AuthenticationService authenticationService,
                                   HakuPermissionService hakuPermissionService) {
        super(dbObjectToHakemusConverter, hakemusToBasicDBObjectConverter);
        this.shaEncrypter = shaEncrypter;
        this.dbObjectToSearchResultItem = dbObjectToSearchResultItem;
        this.authenticationService = authenticationService;
        this.hakuPermissionService = hakuPermissionService;
    }

    @Override
    protected String getCollectionName() {
        return "application";
    }

    @Override
    public List<Application> find(DBObject query) {
        return findApplications(query);
    }

    @Override
    public Application findDraftApplication(final Application application) {
        final DBObject query = toDBObject.apply(application);
        query.put(FIELD_APPLICATION_OID, null);
        return findOneApplication(query);
    }

    @Override
    public List<Application> findByApplicationSystemAndApplicationOption(String asId, String aoId) {
        DBObject dbObject = QueryBuilder.start().and(queryByPreference(Lists.newArrayList(aoId)).get(),
                newOIdExistDBObject(),
                new BasicDBObject(FIELD_APPLICATION_SYSTEM_ID, asId),
                QueryBuilder.start(FIELD_APPLICATION_STATE).in(Lists.newArrayList(
                        Application.State.ACTIVE.toString(), Application.State.INCOMPLETE.toString())).get()).get();
        return findApplications(dbObject);
    }

    public List<Application> findByApplicationOption(List<String> aoIds) {
        DBObject query = QueryBuilder.start().and(queryByPreference(aoIds).get(),
                newOIdExistDBObject(),
                QueryBuilder.start(FIELD_APPLICATION_STATE).is(Application.State.ACTIVE.toString()).get()).get();

        return findApplications(query);
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
        LOG.debug("Enetring findAllQueried");
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
                        QueryBuilder.start("answers.henkilotiedot.Henkilotunnus_digest").is(encryptedSsn).get()
                );
            } else { // Name or date of birth
                queries = addDobOrNameQuery(queries, token);
            }
        }

        QueryBuilder baseQuery = queries.size() > 0 ? QueryBuilder.start().and(queries.toArray(new DBObject[queries.size()])) : QueryBuilder.start();
        DBObject query = newQueryBuilderWithFilters(filters, baseQuery);
        LOG.debug("Constructed query: {}", query.toString());
        return searchApplications(query, applicationQueryParameters.getStart(), applicationQueryParameters.getRows(),
                applicationQueryParameters.getOrderBy(), applicationQueryParameters.getOrderDir());
    }

    private ArrayList<DBObject> addDobOrNameQuery(ArrayList<DBObject> queries, String token) {
        String possibleDob = token.replace(".", "");
        Date dob = tryDate(HETU_DATE, possibleDob);
        if (dob == null) {
            dob = tryDate(LONG_HETU_DATE, possibleDob);
        }
        if (dob != null) {
            queries.add(
                    QueryBuilder.start("answers.henkilotiedot.syntymaaika").is(FORM_DATE.format(dob)).get()
            );
        } else {
            queries.add(
                    QueryBuilder.start("fullName").regex(Pattern.compile(token.toLowerCase())).get()
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
        return QueryBuilder.start().or(
                QueryBuilder.start(FIELD_AO_1).in(aoIds).get(),
                QueryBuilder.start(FIELD_AO_2).in(aoIds).get(),
                QueryBuilder.start(FIELD_AO_3).in(aoIds).get(),
                QueryBuilder.start(FIELD_AO_4).in(aoIds).get(),
                QueryBuilder.start(FIELD_AO_5).in(aoIds).get()
        );
    }

    private QueryBuilder queryByPreference(String preference) {
        return new QueryBuilder().start().or(
                QueryBuilder.start(FIELD_AO_KOULUTUS_ID_1).is(preference).get(),
                QueryBuilder.start(FIELD_AO_KOULUTUS_ID_2).is(preference).get(),
                QueryBuilder.start(FIELD_AO_KOULUTUS_ID_3).is(preference).get(),
                QueryBuilder.start(FIELD_AO_KOULUTUS_ID_4).is(preference).get(),
                QueryBuilder.start(FIELD_AO_KOULUTUS_ID_5).is(preference).get()
        );
    }

    private QueryBuilder queryByLearningOpportunityProviderOid(String lopOid) {
        return QueryBuilder.start().or(
                QueryBuilder.start(FIELD_LOP_1).is(lopOid).get(),
                QueryBuilder.start(FIELD_LOP_2).is(lopOid).get(),
                QueryBuilder.start(FIELD_LOP_3).is(lopOid).get(),
                QueryBuilder.start(FIELD_LOP_4).is(lopOid).get(),
                QueryBuilder.start(FIELD_LOP_5).is(lopOid).get(),
                QueryBuilder.start(FIELD_LOP_PARENTS_1).regex(Pattern.compile(lopOid)).get(),
                QueryBuilder.start(FIELD_LOP_PARENTS_2).regex(Pattern.compile(lopOid)).get(),
                QueryBuilder.start(FIELD_LOP_PARENTS_3).regex(Pattern.compile(lopOid)).get(),
                QueryBuilder.start(FIELD_LOP_PARENTS_4).regex(Pattern.compile(lopOid)).get(),
                QueryBuilder.start(FIELD_LOP_PARENTS_5).regex(Pattern.compile(lopOid)).get()
        );
    }

    private QueryBuilder queryDiscretionaryOnly() {
        return QueryBuilder.start().or(
                QueryBuilder.start(FIELD_DISCRETIONARY_1).is(Boolean.TRUE.toString()).get(),
                QueryBuilder.start(FIELD_DISCRETIONARY_2).is(Boolean.TRUE.toString()).get(),
                QueryBuilder.start(FIELD_DISCRETIONARY_3).is(Boolean.TRUE.toString()).get(),
                QueryBuilder.start(FIELD_DISCRETIONARY_4).is(Boolean.TRUE.toString()).get(),
                QueryBuilder.start(FIELD_DISCRETIONARY_5).is(Boolean.TRUE.toString()).get()
        );
    }

    private Application findOneApplication(DBObject query) {
        DBObject result = getCollection().findOne(query);
        if (result != null) {
            return fromDBObject.apply(result);
        } else {
            throw new ResourceNotFoundExceptionRuntime("Application not found " + query);
        }
    }

    private List<Application> findApplications(DBObject dbObject) {
        final DBCursor dbCursor = getCollection().find(dbObject);
        return Lists.newArrayList(Iterables.transform(dbCursor, fromDBObject));
    }

    private ApplicationSearchResultDTO searchApplications(DBObject query, int start, int rows, String orderBy,
                                                          int orderDir) {
        LOG.debug("Ordering: {}, {}", orderBy, orderDir);
        LOG.debug("Query: {}", query);
        final DBCursor dbCursor = getCollection().find(query).sort(new BasicDBObject(orderBy, orderDir))
                .skip(start).limit(rows).setReadPreference(ReadPreference.secondaryPreferred());
        LOG.debug("Matches: {}", dbCursor.count());
        return new ApplicationSearchResultDTO(dbCursor.count(), Lists.newArrayList(Iterables.transform(dbCursor, dbObjectToSearchResultItem)));
    }

    private DBObject[] buildQueryFilter(final ApplicationQueryParameters applicationQueryParameters) {
        ArrayList<DBObject> filters = new ArrayList<DBObject>(2);
        DBObject stateQuery = null;

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

        String preference = applicationQueryParameters.getAoId();
        if (!isEmpty(preference)) {
            filters.add(queryByPreference(preference).get());
        }

        String aoOid = applicationQueryParameters.getAoOid();
        if (!isEmpty(aoOid)) {
            filters.add(queryByPreference(Lists.newArrayList(aoOid)).get());
        }

        String lopOid = applicationQueryParameters.getLopOid();
        if (!isEmpty(lopOid)) {
            filters.add(queryByLearningOpportunityProviderOid(lopOid).get());
        }

        List<String> asIds = applicationQueryParameters.getAsIds();
        if (!asIds.isEmpty()) {
            filters.add(QueryBuilder.start(FIELD_APPLICATION_SYSTEM_ID).in(asIds).get());
        }

        if (applicationQueryParameters.isDiscretionaryOnly()) {
            filters.add(queryDiscretionaryOnly().get());
        }

        String sendingSchool = applicationQueryParameters.getSendingSchool();
        if (!isEmpty(sendingSchool)) {
            filters.add(QueryBuilder.start(FIELD_SENDING_SCHOOL).is(sendingSchool).get());
        }

        String sendingClass = applicationQueryParameters.getSendingClass();
        if (!isEmpty(sendingClass)) {
            filters.add(QueryBuilder.start(FIELD_SENDING_CLASS).is(sendingClass.toUpperCase()).get());
        }

        filters.add(newOIdExistDBObject());

        return filters.toArray(new DBObject[filters.size()]);
    }

    private ArrayList<DBObject> filterByOrganization() {

        List<String> orgs = authenticationService.getOrganisaatioHenkilo();
        orgs = hakuPermissionService.userCanReadApplications(orgs);

        LOG.debug("OrganisaatioHenkilo.canRead().count() == {} ", orgs.size());
        ArrayList<DBObject> queries = new ArrayList<DBObject>(orgs.size());

        for (String org : orgs) {
            Pattern orgPattern = Pattern.compile(org);
            queries.add(QueryBuilder.start().or(
                    QueryBuilder.start(FIELD_LOP_PARENTS_1).regex(orgPattern).get(),
                    QueryBuilder.start(FIELD_LOP_PARENTS_2).regex(orgPattern).get(),
                    QueryBuilder.start(FIELD_LOP_PARENTS_3).regex(orgPattern).get(),
                    QueryBuilder.start(FIELD_LOP_PARENTS_4).regex(orgPattern).get(),
                    QueryBuilder.start(FIELD_LOP_PARENTS_5).regex(orgPattern).get(),
                    QueryBuilder.start(FIELD_LOP_PARENTS_1).is(null).get()) // Empty applications
                    .get());
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
    }

    @Override
    public Application getNextWithoutStudentOid() {
        DBObject query = new BasicDBObject();
        query.put(FIELD_APPLICATION_OID, new BasicDBObject("$exists", true));
        query.put(FIELD_PERSON_OID, new BasicDBObject("$exists", true));
        query.put(FIELD_STUDENT_OID, new BasicDBObject("$exists", false));
        query.put(FIELD_APPLICATION_STATE, Application.State.ACTIVE.toString());
        query.put("answers.henkilotiedot.Henkilotunnus", new BasicDBObject("$exists", true));
        query.put(FIELD_STUDENT_IDENTIFICATION_DONE, Boolean.FALSE.toString());

        DBObject sortBy = new BasicDBObject(FIELD_LAST_AUTOMATED_PROCESSING_TIME, 1);
        DBObject hint = new BasicDBObject();
        hint.put(FIELD_APPLICATION_STATE, 1);
        hint.put(FIELD_STUDENT_IDENTIFICATION_DONE, 1);
        hint.put(FIELD_LAST_AUTOMATED_PROCESSING_TIME, 1);

        DBCursor cursor = getCollection().find(query).sort(sortBy).hint(hint).limit(1);
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
        if (!cursor.hasNext()) {
            return null;
        }
        return fromDBObject.apply(cursor.next());
    }

    private boolean resultNotEmpty(final DBObject query) {
        return getCollection().find(query).limit(1).size() > 0;
    }
}
