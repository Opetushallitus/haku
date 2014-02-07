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

package fi.vm.sade.haku.oppija.hakemus.service;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationState;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;

public interface ApplicationService {

    Application getApplication(final String applicationSystemId);

    Application getApplicationByOid(final String oid) throws ResourceNotFoundException;

    /**
     * Save answers of a single form phase. Phase is saved to the currently modified application of the user session.
     *
     * @param applicationPhase
     * @return application state
     */
    ApplicationState saveApplicationPhase(final ApplicationPhase applicationPhase);

    /**
     * Submits an application based on current user and form.
     *
     * @return
     */
    String submitApplication(final String applicationSystemId);

    /**
     * Returns all applications where one of the selected application options is the
     * one given as parameter.
     *
     * @param applicationOptionIds list of application option ids
     * @return list of applications
     */
    List<Application> getApplicationsByApplicationOption(List<String> applicationOptionIds);

    /**
     * Return applications that match to given search term. Term is matched against
     * - applications' OID
     * - applicants' ID
     * - applicants' DOB
     * - applicants' hetu
     * - applicants' name
     *
     * @param term
     * @return
     */
    ApplicationSearchResultDTO findApplications(final String term, final ApplicationQueryParameters applicationQueryParameters);


    /**
     * Saves additional info key value pairs to the application
     *
     * @param oid            application oid
     * @param additionalInfo additional info key value pairs
     * @throws ResourceNotFoundException
     */
    void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo) throws ResourceNotFoundException;

    void update(final Application queryApplication, final Application application);

    /**
     * Gets the value of the specified application and key
     *
     * @param applicationOid application oid
     * @param key
     * @return value of the key
     * @throws ResourceNotFoundException
     */
    String getApplicationKeyValue(final String applicationOid, final String key) throws ResourceNotFoundException;

    /**
     * Puts a additional information key value pair to the application
     *
     * @param applicationOid application oid
     * @param key            key of the additional information
     * @param value          value of the key
     * @throws ResourceNotFoundException
     */
    void putApplicationAdditionalInfoKeyValue(final String applicationOid, final String key, final String value) throws ResourceNotFoundException;

    /**
     * Finds next application without user oid. Returns matching application or null, if none found.
     *
     * @return Application or null
     */
    Application getNextSubmittedApplication();

    /**
     * Set proper user for this application. If user can be authenticated, activate application. Otherwise, set
     * application as incomplete.
     *
     * @param application to process
     * @return processed application
     */
    Application addPersonOid(Application application);

    /**
     * Set proper user for this application. If user can be authenticated, activate application. Otherwise, set
     * application as incomplete.
     *
     * @param oid of application to process
     * @return processed application
     */

    Application passivateApplication(String oid);

    Application checkStudentOid(Application application);

    /**
     * Creates a new empty application to specified application system.
     *
     * @param asId application system id
     * @return created application
     */
    Application officerCreateNewApplication(final String asId);

    Application fillLOPChain(Application application, boolean save);

    Application getNextWithoutStudentOid();

    void addNote(final Application application, final String noteText, final boolean persist);

    Application activateApplication(String oid);

    Application getSubmittedApplication(final String applicationSystemId, final String oid);

    Application addSendingSchool(Application application);
}
