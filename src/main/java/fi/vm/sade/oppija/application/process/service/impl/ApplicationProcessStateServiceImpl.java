/*
 *
 *  * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *  *
 *  * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 *  * soon as they will be approved by the European Commission - subsequent versions
 *  * of the EUPL (the "Licence");
 *  *
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * European Union Public Licence for more details.
 *
 */

package fi.vm.sade.oppija.application.process.service.impl;

import fi.vm.sade.oppija.application.process.dao.ApplicationProcessStateDAO;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessStateStatus;
import fi.vm.sade.oppija.application.process.service.ApplicationProcessStateService;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Mikko Majapuro
 */
@Service
public class ApplicationProcessStateServiceImpl implements ApplicationProcessStateService {

    private final ApplicationProcessStateDAO applicationProcessStateDAO;

    @Autowired
    public ApplicationProcessStateServiceImpl(@Qualifier("applicationProcessStateDAOMongoImpl") ApplicationProcessStateDAO applicationProcessStateDAO) {
        this.applicationProcessStateDAO = applicationProcessStateDAO;
    }

    @Override
    public void setApplicationProcessStateStatus(String oid, ApplicationProcessStateStatus status) {
        ApplicationProcessState query = new ApplicationProcessState(oid, null);
        ApplicationProcessState applicationProcessState = this.applicationProcessStateDAO.findOne(query);
        if (applicationProcessState != null) {
            this.applicationProcessStateDAO.update(query, applicationProcessState);
        } else {
            //TODO check oid
            applicationProcessState = new ApplicationProcessState(oid, status.getName());
            this.applicationProcessStateDAO.create(applicationProcessState);
        }
    }

    @Override
    public ApplicationProcessState get(String oid) {
        ApplicationProcessState query = new ApplicationProcessState(oid, null);
        ApplicationProcessState applicationProcessState = this.applicationProcessStateDAO.findOne(query);
        if (applicationProcessState == null) {
            throw new ResourceNotFoundException("Could not find application process state by oid" + oid);
        } else {
            return applicationProcessState;
        }
    }
}
