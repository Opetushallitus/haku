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
import fi.vm.sade.oppija.lomake.domain.User;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
@Service("applicationDAOMongoImpl")
public class ApplicationDAOMongoImpl extends AbstractDAOMongoImpl<Application> implements ApplicationDAO {

    private static final String OID_PREFIX = "1.2.3.4.5.";

    public ApplicationDAOMongoImpl() {
        super(new DBObjectToApplicationFunction(), new ApplicationToDBObjectFunction());
    }

    @Override
    public ApplicationState tallennaVaihe(ApplicationState state) {
        Application queryApplication = new Application(state.getHakemus().getFormId(), state.getHakemus().getUser());
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
    public String submit(final Application application) {
        final DBObject query = toDBObject.apply(application);
        String oid = getNewOid();
        DBObject update = new BasicDBObject("$set", new BasicDBObject(Application.OID, oid));
        getCollection().update(query, update);
        return oid;
    }

    @Override
    public Application findPendingApplication(final Application application) {
        final DBObject query = toDBObject.apply(application);
        User user = application.getUser();
        if (user.isKnown()) {
            query.put("oid", new BasicDBObject("$exists", false));
        }
        return findOneApplication(query);
    }

    public String getNewOid() {
        return OID_PREFIX + getNextId();
    }


    private Application findOneApplication(DBObject query) {
        List<Application> listOfApplications = findApplications(query);
        if (listOfApplications.size() == 1) {
            return listOfApplications.get(0);
        }
        throw new ResourceNotFoundException("Application not found " + query);
    }

    private List<Application> findApplications(DBObject dbObject) {
        final DBCursor dbCursor = getCollection().find(dbObject);
        return Lists.newArrayList(Iterables.transform(dbCursor, fromDBObject));
    }

}
