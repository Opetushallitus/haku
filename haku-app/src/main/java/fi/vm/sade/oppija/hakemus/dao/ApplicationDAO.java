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

    /**
     * Return list of applications that match given model application, state and
     *
     * @param application
     * @return
     */
    List<Application> find(Application application);

    /**
     * Returns Applications that are included in specified application system.
     *
     * @param asId application system ids
     * @return list of applications or an empty list if none are found
     */
    List<Application> findByApplicationSystem(String asId);

    /**
     * Returns applications that apply to this application option, ie.
     * applications where one of the selected application options is the
     * one given as parameter.
     *
     * @param aoIds list of application option identifiers
     * @return list of applications or an empty list if none are found
     */
    List<Application> findByApplicationOption(List<String> aoIds);

    /**
     * Checks if submitted application already exists by specified social security number and
     * application system
     *
     * @param asId application system id
     * @param ssn  social security number
     * @return true if application exists, false otherwise
     */
    boolean checkIfExistsBySocialSecurityNumber(String asId, String ssn);

    /**
     * Return all applications where applicants first name or last name contains
     * given term, case insensitive.
     *
     * @param term
     * @return
     */
    List<Application> findByApplicantName(String term, ApplicationQueryParameters applicationQueryParameters);

    /**
     * Return all applications where applicants ssn matches given term.
     *
     * @param term
     * @return
     */
    List<Application> findByApplicantSsn(String term, ApplicationQueryParameters applicationQueryParameters);

    /**
     * Return applications which oid or applicants' oid matches given term.
     *
     * @param applicationQueryParameters
     * @param term
     * @return
     */
    List<Application> findByOid(String term, ApplicationQueryParameters applicationQueryParameters);

    List<Application> findByApplicationOid(String term, ApplicationQueryParameters applicationQueryParameters);

    List<Application> findByUserOid(String term, ApplicationQueryParameters applicationQueryParameters);

    List<Application> findAllFiltered(ApplicationQueryParameters applicationQueryParameters);

    List<Application> findByApplicantDob(String term, ApplicationQueryParameters applicationQueryParameters);

}
