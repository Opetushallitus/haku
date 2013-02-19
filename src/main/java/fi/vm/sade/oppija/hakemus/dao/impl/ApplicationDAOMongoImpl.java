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

package fi.vm.sade.oppija.hakemus.dao.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import fi.vm.sade.oppija.common.dao.AbstractDAOMongoImpl;
import fi.vm.sade.oppija.hakemus.converter.ApplicationToDBObjectFunction;
import fi.vm.sade.oppija.hakemus.converter.DBObjectToApplicationFunction;
import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.EncrypterService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl<Application> implements ApplicationDAO {

    private final EncrypterService shaEncrypter;

    private static final String FIELD_AO_1 = "answers.hakutoiveet.preference1-Koulutus-id";
    private static final String FIELD_AO_2 = "answers.hakutoiveet.preference2-Koulutus-id";
    private static final String FIELD_AO_3 = "answers.hakutoiveet.preference3-Koulutus-id";
    private static final String FIELD_AO_4 = "answers.hakutoiveet.preference4-Koulutus-id";
    private static final String FIELD_AO_5 = "answers.hakutoiveet.preference5-Koulutus-id";

    private static final String FIELD_LOP_1 = "answers.hakutoiveet.preference1-Opetuspiste-id";
    private static final String FIELD_LOP_2 = "answers.hakutoiveet.preference2-Opetuspiste-id";
    private static final String FIELD_LOP_3 = "answers.hakutoiveet.preference3-Opetuspiste-id";
    private static final String FIELD_LOP_4 = "answers.hakutoiveet.preference4-Opetuspiste-id";
    private static final String FIELD_LOP_5 = "answers.hakutoiveet.preference5-Opetuspiste-id";

    private static final String FIELD_APPLICATION_OID = "oid";
    private static final String FIELD_PERSON_OID = "personOid";
    private static final String FIELD_APPLICATION_STATE = "state";

    @Value("${application.oid.prefix}")
    private String applicationOidPrefix;
    @Value("${user.oid.prefix}")
    private String userOidPrefix;

    @Autowired
    public ApplicationDAOMongoImpl(DBObjectToApplicationFunction dbObjectToHakemusConverter,
                                   ApplicationToDBObjectFunction hakemusToBasicDBObjectConverter,
                                   @Qualifier("shaEncrypter") EncrypterService shaEncrypter) {
        super(dbObjectToHakemusConverter, hakemusToBasicDBObjectConverter);
        this.shaEncrypter = shaEncrypter;
    }

    @Override
    public ApplicationState tallennaVaihe(ApplicationState state) {

        Application queryApplication = new Application(state.getHakemus().getFormId(), state.getHakemus().getUser(),
                state.getHakemus().getOid());
        queryApplication.activate();
        final DBObject query = toDBObject.apply(queryApplication);

        DBObject one = getCollection().findOne(query);
        if (one != null) {
            queryApplication = fromDBObject.apply(one);
        }
        Application uusiApplication = state.getHakemus();
        Map<String, String> answersMerged = uusiApplication.getVastauksetMerged();
        queryApplication.addVaiheenVastaukset(state.getVaiheId(), answersMerged);
        queryApplication.setPhaseId(uusiApplication.getPhaseId());

        one = toDBObject.apply(queryApplication);
        getCollection().update(query, one, true, false);

        return state;
    }

    @Override
    protected String getCollectionName() {
        return "application";
    }

    @Override
    public Application findDraftApplication(final Application application) {
        final DBObject query = toDBObject.apply(application);
        query.put(FIELD_APPLICATION_OID, new BasicDBObject("$exists", false));
        return findOneApplication(query);
    }

    @Override
    public List<Application> findByApplicationSystem(String asId) {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("formId.applicationPeriodId", asId);
        List<Application> applications = findApplications(dbObject);

        return applications;
    }

    public List<Application> findByApplicationOption(String aoId) {
        DBObject query = queryByPreference(aoId).get();
        return findApplications(query);
    }

    @Override
    public boolean checkIfExistsBySocialSecurityNumber(String asId, String ssn) {
        if (ssn != null) {
            final DBObject query = new BasicDBObject("formId.applicationPeriodId", asId)
                    .append("answers.henkilotiedot." + SocialSecurityNumber.HENKILOTUNNUS_HASH, shaEncrypter.encrypt(ssn))
                    .append(FIELD_APPLICATION_OID, new BasicDBObject("$exists", true));
            return getCollection().count(query) > 0;
        }
        return false;
    }

    @Override
    public List<Application> findAllFiltered(ApplicationQueryParameters applicationQueryParameters) {
        DBObject[] filters = buildQueryFilter(applicationQueryParameters);
        QueryBuilder baseQuery = QueryBuilder.start();
        DBObject query;
        if (filters.length > 0) {
            query = QueryBuilder.start().and(baseQuery.get(), QueryBuilder.start().or(filters).get()).get();
        } else {
            query = baseQuery.get();
        }
        return findApplications(query);
    }

    private QueryBuilder queryByPreference(String aoId) {
        return QueryBuilder.start().or(
                QueryBuilder.start(FIELD_AO_1).is(aoId).get(),
                QueryBuilder.start(FIELD_AO_2).is(aoId).get(),
                QueryBuilder.start(FIELD_AO_3).is(aoId).get(),
                QueryBuilder.start(FIELD_AO_4).is(aoId).get(),
                QueryBuilder.start(FIELD_AO_5).is(aoId).get()
        );
    }

    private QueryBuilder queryByLearningOpportunityProviderOid(String LOPOid) {
        return QueryBuilder.start().or(
                QueryBuilder.start(FIELD_LOP_1).is(LOPOid).get(),
                QueryBuilder.start(FIELD_LOP_2).is(LOPOid).get(),
                QueryBuilder.start(FIELD_LOP_3).is(LOPOid).get(),
                QueryBuilder.start(FIELD_LOP_4).is(LOPOid).get(),
                QueryBuilder.start(FIELD_LOP_5).is(LOPOid).get()
        );
    }

    private Application findOneApplication(DBObject query) {
        List<Application> listOfApplications = findApplications(query);
        if (listOfApplications.size() == 1) {
            return listOfApplications.get(0);
        } else if (listOfApplications.size() > 1) {
            throw new ResourceNotFoundExceptionRuntime("Found two or more applications found " + query);
        }
        throw new ResourceNotFoundExceptionRuntime("Application not found " + query);
    }

    private List<Application> findApplications(DBObject dbObject) {
        final DBCursor dbCursor = getCollection().find(dbObject);
        return Lists.newArrayList(Iterables.transform(dbCursor, fromDBObject));
    }

    @Override
    public List<Application> findByApplicantName(String term, ApplicationQueryParameters applicationQueryParameters) {
        DBObject query;
        DBObject[] filters = buildQueryFilter(applicationQueryParameters);
        Pattern namePattern = Pattern.compile(term, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        QueryBuilder baseQuery = QueryBuilder.start().or(
                QueryBuilder.start("answers.henkilotiedot.Etunimet").regex(namePattern).get(),
                QueryBuilder.start("answers.henkilotiedot.Sukunimi").regex(namePattern).get());
        if (filters.length > 0) {
            query = QueryBuilder.start().and(baseQuery.get(), QueryBuilder.start().or(filters).get()).get();
        } else {
            query = baseQuery.get();
        }
        return findApplications(query);
    }

    @Override
    public List<Application> findByOid(String term, ApplicationQueryParameters applicationQueryParameters) {
        DBObject[] filters = buildQueryFilter(applicationQueryParameters);
        QueryBuilder baseQuery;
        if (term.startsWith(applicationOidPrefix)) {
            baseQuery = QueryBuilder.start(FIELD_APPLICATION_OID).is(term);
        } else if (term.startsWith(userOidPrefix)) {
            baseQuery = QueryBuilder.start(FIELD_PERSON_OID).is(term);
        } else {
            baseQuery = QueryBuilder.start().or(
                    QueryBuilder.start(FIELD_APPLICATION_OID).is(applicationOidPrefix + "." + term).get(),
                    QueryBuilder.start(FIELD_PERSON_OID).is(userOidPrefix + "." + term).get());
        }
        DBObject query;
        if (filters.length > 0) {
            query = QueryBuilder.start().and(baseQuery.get(), QueryBuilder.start().or(filters).get()).get();
        } else {
            query = baseQuery.get();
        }
        return findApplications(query);
    }

    @Override
    public List<Application> findByApplicationOid(String term, ApplicationQueryParameters applicationQueryParameters) {
        DBObject[] filters = buildQueryFilter(applicationQueryParameters);
        QueryBuilder baseQuery = QueryBuilder.start(FIELD_APPLICATION_OID).is(term);
        DBObject query;
        if (filters.length > 0) {
            query = QueryBuilder.start().and(baseQuery.get(), QueryBuilder.start().or(filters).get()).get();
        } else {
            query = baseQuery.get();
        }
        return findApplications(query);
    }

    @Override
    public List<Application> findByUserOid(String term, ApplicationQueryParameters applicationQueryParameters) {
        DBObject[] filters = buildQueryFilter(applicationQueryParameters);
        QueryBuilder baseQuery = QueryBuilder.start(FIELD_PERSON_OID).is(term);
        DBObject query;
        if (filters.length > 0) {
            query = QueryBuilder.start().and(baseQuery.get(), QueryBuilder.start().or(filters).get()).get();
        } else {
            query = baseQuery.get();
        }
        return findApplications(query);
    }

    @Override
    public List<Application> findByApplicantSsn(String term, ApplicationQueryParameters applicationQueryParameters) {
        DBObject[] filters = buildQueryFilter(applicationQueryParameters);
        QueryBuilder baseQuery = QueryBuilder.start("answers.henkilotiedot." + SocialSecurityNumber.HENKILOTUNNUS_HASH)
                .is(shaEncrypter.encrypt(term));
        DBObject query;
        if (filters.length > 0) {
            query = QueryBuilder.start().and(baseQuery.get(), QueryBuilder.start().or(filters).get()).get();
        } else {
            query = baseQuery.get();
        }
        return findApplications(query);
    }

    @Override
    public List<Application> findByApplicantDob(final String term, final ApplicationQueryParameters applicationQueryParameters) {
        DBObject[] filters = buildQueryFilter(applicationQueryParameters);
        String dob = hetuDobToIsoDate(term);
        QueryBuilder baseQuery = QueryBuilder.start("answers.henkilotiedot.syntymaaika").is(dob);
        DBObject query;
        if (filters.length > 0) {
            query = QueryBuilder.start().and(baseQuery.get(), QueryBuilder.start().or(filters).get()).get();
        } else {
            query = baseQuery.get();
        }
        return findApplications(query);
    }

    private String hetuDobToIsoDate(String term) {
        DateFormat dobFmt = new SimpleDateFormat("ddMMyy");
        DateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd");
        dobFmt.setLenient(false);
        try {
            Date dob = dobFmt.parse(term);
            if (new Date().before(dob)) {
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(dob);
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 100);
                dob = cal.getTime();
            }
            return isoFmt.format(dob);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Can not parse " + term + " as date", e);
        }
    }

    private DBObject[] buildQueryFilter(final ApplicationQueryParameters applicationQueryParameters) {
        ArrayList<DBObject> filters = new ArrayList<DBObject>(2);
        DBObject stateQuery = null;

        String state = applicationQueryParameters.getState();
        if (!isEmpty(state)) {
            for (Application.State s : Application.State.values()) {
                if (Application.State.valueOf(state).equals(s)) {
                    if (applicationQueryParameters.isFetchPassive() && !s.equals(Application.State.PASSIVE)) {
                        stateQuery = QueryBuilder
                                .start().or(QueryBuilder.start(FIELD_APPLICATION_STATE).is(s.toString()).get(),
                                        QueryBuilder.start(FIELD_APPLICATION_STATE).is(Application.State.PASSIVE.toString()).get()).get();

                    } else {
                        stateQuery = QueryBuilder.start(FIELD_APPLICATION_STATE).is(state).get();
                    }
                    break;
                }
            }
        }

        if (stateQuery != null) {
            filters.add(stateQuery);
        }

        String preference = applicationQueryParameters.getPreference();
        if (!isEmpty(preference)) {
            filters.add(queryByPreference(preference).get());
        }
        String lopOid = applicationQueryParameters.getLOPOid();
        if (!isEmpty(lopOid)) {
            filters.add(queryByLearningOpportunityProviderOid(lopOid).get());
        }
        return filters.toArray(new DBObject[filters.size()]);
    }
}
