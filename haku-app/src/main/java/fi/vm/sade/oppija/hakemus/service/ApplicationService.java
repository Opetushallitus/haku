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

package fi.vm.sade.oppija.hakemus.service;

import fi.vm.sade.oppija.hakemus.dao.ApplicationQueryParameters;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationInfo;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;

import java.util.List;
import java.util.Map;

public interface ApplicationService {

    Application getApplication(FormId formId);

    /**
     * Save answers of a single form phase. Phase is saved to the currently modified application of the user session.
     *
     * @param applicationPhase
     * @param skipValidators   set to true, if you want the phase to be saved regardless of validation results
     * @return application state
     */
    ApplicationState saveApplicationPhase(final ApplicationPhase applicationPhase, boolean skipValidators);

    /**
     * Save answers of a single form phase. Phase is saved to the application with the parameter oid.
     *
     * @param applicationPhase
     * @param oid
     * @param skipValidators   set to true, if you want the phase to be saved regardless of validation results
     * @return application state
     */
    ApplicationState saveApplicationPhase(final ApplicationPhase applicationPhase, final String oid, final boolean skipValidators);

    /**
     * Retrieve application by oid.
     *
     * @param oid
     * @return application
     * @throws ResourceNotFoundException thrown when an application is not found
     *                                   with the given oid
     */
    Application getApplication(String oid) throws ResourceNotFoundException;

    /**
     * Submits an application based on current user and form.
     *
     * @param formId
     * @return
     */
    String submitApplication(final FormId formId);

    /**
     * @param hakuLomakeId
     * @param oid
     * @return
     * @throws ResourceNotFoundException if an application is not found with the oid
     */
    Application getPendingApplication(final FormId hakuLomakeId, final String oid) throws ResourceNotFoundException;

    /**
     * Retrieves all submitted applications related to specified application system
     *
     * @param applicationSystemId
     * @return list of applications
     */
    List<Application> getApplicationsByApplicationSystem(String applicationSystemId);

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
    List<Application> findApplications(final String term, final ApplicationQueryParameters applicationQueryParameters);


    /**
     * Saves additional info key value pairs to the application
     *
     * @param oid            application oid
     * @param additionalInfo additional info key value pairs
     * @throws ResourceNotFoundException
     */
    void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo) throws ResourceNotFoundException;

    /**
     * Method to get the oids of the application preferences
     *
     * @param applicationOid application oid
     * @return list of application preference oids
     * @throws ResourceNotFoundException
     */
    List<String> getApplicationPreferenceOids(final String applicationOid) throws ResourceNotFoundException;

    /**
     * Method to get the oids of the application preferences
     *
     * @param application application containing all the data
     * @return list of application preference oids
     */
    List<String> getApplicationPreferenceOids(final Application application);

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
     * @param applicationOid application oid
     * @param key key of the additional information
     * @param value value of the key
     * @throws ResourceNotFoundException
     */
    void putApplicationAdditionalInfoKeyValue(final String applicationOid, final String key, final String value) throws ResourceNotFoundException;

}
