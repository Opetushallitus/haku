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

package fi.vm.sade.oppija.application.process.dao.impl;

import com.mongodb.DBObject;
import fi.vm.sade.oppija.application.process.converter.ApplicationProcessStateToDBObject;
import fi.vm.sade.oppija.application.process.converter.DBObjectToApplicationProcessState;
import fi.vm.sade.oppija.application.process.dao.ApplicationProcessStateDAO;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import fi.vm.sade.oppija.common.dao.AbstractDAOMongoImpl;
import org.springframework.stereotype.Service;

/**
 * @author Mikko Majapuro
 */
@Service("applicationProcessStateDAOMongoImpl")
public class ApplicationProcessStateDAOMongoImpl extends AbstractDAOMongoImpl<ApplicationProcessState> implements ApplicationProcessStateDAO {

    private static final String COLLECTION_NAME = "applicationProcessState";

    public ApplicationProcessStateDAOMongoImpl() {
        super(new DBObjectToApplicationProcessState(), new ApplicationProcessStateToDBObject());
    }

    @Override
    protected String getCollectionName() {
        return COLLECTION_NAME;
    }

    @Override
    public void create(ApplicationProcessState applicationProcessState) {
        ApplicationProcessState query = new ApplicationProcessState(applicationProcessState.getOid(), null);
        final DBObject dbQuery = toDBObject.apply(query);
        DBObject result = getCollection().findOne(dbQuery);
        if (result != null) {
            throw new IllegalStateException("Application process state already found " + query);
        }
        final DBObject dbInsert = toDBObject.apply(applicationProcessState);
        getCollection().insert(dbInsert);
    }

    @Override
    public ApplicationProcessState findOne(ApplicationProcessState query) {
        final DBObject dbQuery = toDBObject.apply(query);
        DBObject result = getCollection().findOne(dbQuery);
        if (result != null) {
            return fromDBObject.apply(result);
        }
        return null;
    }
}
