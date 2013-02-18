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

import java.util.List;
import java.util.Map;

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationInfo;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;

public interface ApplicationService {

    List<ApplicationInfo> getUserApplicationInfo();

    Application getApplication(FormId formId);

    /**
     * Save answers of a single form phase. Phase is saved to the currently modified application of the user session.
     *
     * @param applicationPhase
     * @return appplication state
     */
    ApplicationState saveApplicationPhase(final ApplicationPhase applicationPhase);

    /**
     * Save answers of a single form phase. Phase is saved to the application with the parameter oid.
     *
     * @param applicationPhase
     * @param oid
     * @return application state
     */
    ApplicationState saveApplicationPhase(final ApplicationPhase applicationPhase, final String oid);

    /**
     * Save answers of a single form phase. Phase is saved to the parameter application.
     *
     * @param applicationPhase
     * @param application
     * @return application state
     */
    ApplicationState saveApplicationPhase(final ApplicationPhase applicationPhase, final Application application);

    /**
     * Retrieve application by oid.
     *
     * @param oid
     * @return application
     * @throws ResourceNotFoundException thrown when an application is not found
     * with the given oid
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
     *
     *
     * @param hakuLomakeId
     * @param oid
     * @return
     * @throws ResourceNotFoundException if an application is not found with the oid
     */
    Application getPendingApplication(final FormId hakuLomakeId, final String oid) throws ResourceNotFoundException;

    /**
     * Retrieves all submitted applications related to a single application system
     *
     * @param applicationSystemId
     * @return
     */
    List<Application> getApplicationsByApplicationSystem(String applicationSystemId);

    /**
     * Returns all applications where one of the selected application options is the
     * one given as parameter.
     *
     * @param applicationOptionId application option id
     * @return list of applications
     */
    List<Application> getApplicationsByApplicationOption(String applicationOptionId);

    /**
     * Return applications that match to given search term. Term is matched against
     *      - applications' OID 
     *      - applicants' ID
     *      - applicants' DOB
     *      - applicants' hetu
     *      - applicants' name
     * 
     * @param term
     * @return
     */
    List<Application> findApplications(String term, String state, boolean fetchPassive, String preference);


    /**
     * Saves additional info key value pairs to the application
     * @param oid application oid
     * @param additionalInfo additional info key value pairs
     * @throws ResourceNotFoundException
     */
    void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo) throws ResourceNotFoundException;

    /**
     * Method to get the oids of the application preferences
     * @param applicationOid application oid
     * @return list of application preference oids
     * @throws ResourceNotFoundException
     */
    List<String> getApplicationPreferenceOids(final String applicationOid) throws ResourceNotFoundException;

    /**
     * Method to get the oids of the application preferences
     * @param application application containing all the data
     * @return list of application preference oids
     */
    List<String> getApplicationPreferenceOids(final Application application);
}
