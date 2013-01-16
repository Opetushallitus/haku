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
import fi.vm.sade.oppija.common.dao.AbstractDAOMongoImpl;
import fi.vm.sade.oppija.hakemus.converter.ApplicationToDBObjectFunction;
import fi.vm.sade.oppija.hakemus.converter.DBObjectToApplicationFunction;
import fi.vm.sade.oppija.hakemus.dao.ApplicationDAO;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundExceptionRuntime;
import fi.vm.sade.oppija.lomake.service.EncrypterService;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl<Application> implements ApplicationDAO {

    private static final String OID_PREFIX = "1.2.3.4.5.";
    private final EncrypterService shaEncrypter;


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
        final DBObject query = toDBObject.apply(queryApplication);

        DBObject one = getCollection().findOne(query);
        if (one != null) {
            queryApplication = fromDBObject.apply(one);
        }
        Application uusiApplication = state.getHakemus();
        Map<String, String> vastauksetMerged = uusiApplication.getVastauksetMerged();
        queryApplication.addVaiheenVastaukset(state.getVaiheId(), vastauksetMerged);
        queryApplication.setVaiheId(uusiApplication.getVaiheId());

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
        query.put("oid", new BasicDBObject("$exists", false));
        return findOneApplication(query);
    }

    @Override
    public String getNewOid() {
        return OID_PREFIX + getNextId();
    }

    @Override
    public List<Application> findByApplicationSystem(String asId) {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("formId.applicationPeriodId", asId);
        List<Application> applications = findApplications(dbObject);


        return applications;
    }

    @Override
    public boolean checkIfExistsBySocialSecurityNumber(String asId, String ssn) {
        if (asId != null && ssn != null) {
            final DBObject query = new BasicDBObject("formId.applicationPeriodId", asId)
                    .append("vastaukset.henkilotiedot.Henkilotunnus_digest", shaEncrypter.encrypt(ssn))
                    .append("oid", new BasicDBObject("$exists", true));
            return getCollection().count(query) > 0;
        }
        return false;
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

}
