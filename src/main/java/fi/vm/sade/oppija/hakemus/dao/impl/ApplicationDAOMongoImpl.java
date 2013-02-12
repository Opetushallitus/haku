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

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.EncrypterService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;

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

    @Autowired
    public ApplicationDAOMongoImpl(DBObjectToApplicationFunction dbObjectToHakemusConverter, ApplicationToDBObjectFunction hakemusToBasicDBObjectConverter,
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
    public List<Application> find(Application application, String state, boolean fetchPassive, String preference) {
        
        return find(application);
    }
    
    @Override
    public Application findDraftApplication(final Application application) {
        final DBObject query = toDBObject.apply(application);
        query.put("oid", new BasicDBObject("$exists", false));
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
                    .append("oid", new BasicDBObject("$exists", true));
            return getCollection().count(query) > 0;
        }
        return false;
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
    public List<Application> findByApplicantName(String term, String state, boolean fetchPassive, String preference) {
        Pattern namePattern = Pattern.compile(term, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        DBObject query = QueryBuilder.start().and( 
                QueryBuilder.start().or(
                        QueryBuilder.start("answers.henkilotiedot.Etunimet").regex(namePattern).get(),
                        QueryBuilder.start("answers.henkilotiedot.Sukunimi").regex(namePattern).get()).get(),
                QueryBuilder.start().or(buildQueryFilter(state, fetchPassive, preference)).get()
                ).get();
        return findApplications(query);
    }

    @Override
    public List<Application> findByApplicantSsn(String term, String state, boolean fetchPassive, String preference) {

        DBObject query = QueryBuilder.start().and(
                QueryBuilder.start("answers.henkilotiedot." + SocialSecurityNumber.HENKILOTUNNUS_HASH)
                    .is(shaEncrypter.encrypt(term)).get(),
                QueryBuilder.start().or(buildQueryFilter(state, fetchPassive, preference)).get()).get();
        return findApplications(query);
    }

    private DBObject[] buildQueryFilter(String state, boolean fetchPassive, String preference) {
        ArrayList<DBObject> filters = new ArrayList<DBObject>(2);
       DBObject stateQuery = null;
        if (!isEmpty(state)) {
             for (Application.State s : Application.State.values()) {
                if (Application.State.valueOf(state).equals(s)) {
                    stateQuery = QueryBuilder.start("state").is(state).get();
                    break;
                }
            }
        } else if (fetchPassive) {
            stateQuery = QueryBuilder.start().or(
                    QueryBuilder.start("state").is(Application.State.ACTIVE.toString()).get(),
                    QueryBuilder.start("state").is(Application.State.PASSIVE.toString()).get()).get();
        } else {
            stateQuery = QueryBuilder.start("state").is(Application.State.ACTIVE.toString()).get();
        }
            
        filters.add(stateQuery);
        
        if (!isEmpty(preference)) {
            filters.add(queryByPreference(preference).get());
        }
        return filters.toArray(new DBObject[filters.size()]);
    }

}
