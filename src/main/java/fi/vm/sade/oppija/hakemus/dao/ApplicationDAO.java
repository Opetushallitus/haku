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

package fi.vm.sade.oppija.hakemus.dao;

import fi.vm.sade.oppija.common.dao.BaseDAO;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;

import java.util.List;

/**
 * DAO interface for saving, updating and finding applications made by users.
 *
 * @author Hannu Lyytikainen
 */
public interface ApplicationDAO extends BaseDAO<Application> {
    ApplicationState tallennaVaihe(ApplicationState state);

    Application findDraftApplication(Application application);

    String getNewOid();

    List<Application> findByApplicationSystem(String asId);

    /**
     * Checks if submitted application already exists by specified social security number and
     * application system
     * @param asId application system id
     * @param ssn social security number
     * @return true if application exists, false otherwise
     */
    boolean checkIfExistsBySocialSecurityNumber(String asId, String ssn);
}
