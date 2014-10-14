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
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationState;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ApplicationService {

    Application getApplication(final String applicationSystemId);

    Map<String, String> ensureApplicationOptionGroupData(Map<String, String> answers);

    Application ensureApplicationOptionGroupData(Application application);

    Application getApplication(final Application queryApplication);

    Application getApplicationByOid(final String oid);

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
     * @return Application
     */
    Application submitApplication(final String applicationSystemId, String language);

    ApplicationSearchResultDTO findApplications(final ApplicationQueryParameters applicationQueryParameters);

    List<ApplicationAdditionalDataDTO> findApplicationAdditionalData(final String applicationSystemId, final String aoId);

    void saveApplicationAdditionalInfo(final List<ApplicationAdditionalDataDTO> applicationAdditionalData);


    /**
     * Saves additional info key value pairs to the application
     *
     * @param oid            application oid
     * @param additionalInfo additional info key value pairs
     */
    void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo);

    void update(final Application queryApplication, final Application application);

    /**
     * Gets the value of the specified application and key
     *
     * @param applicationOid application oid
     * @param key
     * @return value of the key
     */
    String getApplicationKeyValue(final String applicationOid, final String key);

    /**
     * Puts a additional information key value pair to the application
     *
     * @param applicationOid application oid
     * @param key            key of the additional information
     * @param value          value of the key
     */
    void putApplicationAdditionalInfoKeyValue(final String applicationOid, final String key, final String value);

    /**
     * Set proper user for this application. If user can be authenticated, activate application. Otherwise, set
     * application as incomplete.
     *
     * @param application to process
     * @return processed application
     */
    Application addPersonOid(Application application);

    Application checkStudentOid(Application application);

    /**
     * Creates a new empty application to specified application system.
     *
     * @param asId application system id
     * @return created application
     */
    Application officerCreateNewApplication(final String asId);

    Application getSubmittedApplication(final String applicationSystemId, final String oid);

    List<Map<String, Object>> findFullApplications(final ApplicationQueryParameters applicationQueryParameters);

    Application updateAuthorizationMeta(Application application, boolean save) throws IOException;

    Application updatePreferenceBasedData(final Application application);
}
