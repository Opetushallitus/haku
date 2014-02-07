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

package fi.vm.sade.haku.oppija.hakemus.it.dao;

import com.mongodb.DBObject;
import fi.vm.sade.haku.oppija.common.dao.BaseDAO;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;

import java.util.List;

/**
 * DAO interface for saving, updating and finding applications made by users.
 *
 * @author Hannu Lyytikainen
 */
public interface ApplicationDAO extends BaseDAO<Application> {

    Application findDraftApplication(Application application);

    /**
     * Return list of applications that match given model application, state and
     *
     * @param application
     * @return
     */
    List<Application> find(Application application);

    /**
     * Return list of applications that match given query
     *
     * @param query
     * @return
     */
    List<Application> find(DBObject query);

    /**
     * Finds all the applications related to given application system and application option.
     * Matching applications must be ACTIVE and contains application OID
     *
     * @param asId application system id
     * @param aoId application option id
     * @return list of applications
     */
    List<Application> findByApplicationSystemAndApplicationOption(String asId, String aoId);

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
     * Checks if submitted application already exists by specified social security number, application option and
     * application system
     *
     * @param asId application system id
     * @param ssn  social security number
     * @param aoId application option oid
     * @return true if application already exists, false otherwise
     */
    boolean checkIfExistsBySocialSecurityNumberAndAo(String asId, String ssn, String aoId);

    /**
     * Updates key/value of the application by oid
     *
     * @param oid   application oid
     * @param key   key to be updated, including full hierarchy of the key "additionalInfo.foo"
     * @param value value of the given key
     */
    void updateKeyValue(final String oid, final String key, final String value);

    Application getNextWithoutStudentOid();

    Application getNextSubmittedApplication();

    ApplicationSearchResultDTO findAllQueried(String term, ApplicationQueryParameters applicationQueryParameters);
}
