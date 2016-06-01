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
import fi.vm.sade.haku.oppija.hakemus.domain.dto.UpdatePreferenceResult;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationQueryParameters;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationState;
import fi.vm.sade.haku.virkailija.valinta.ValintaServiceCallFailedException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ApplicationService {

    Application getApplication(final String applicationSystemId);

    Map<String, String> ensureApplicationOptionGroupData(Map<String, String> answers, String lang);

    Application postProcessApplicationAnswers(Application application) throws ValintaServiceCallFailedException;

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
    List<ApplicationAdditionalDataDTO> findApplicationAdditionalData(final List<String> oids);

    void saveApplicationAdditionalInfo(final List<ApplicationAdditionalDataDTO> applicationAdditionalData);


    /**
     * Saves additional info key value pairs to the application
     *
     * @param oid            application oid
     * @param additionalInfo additional info key value pairs
     */
    void saveApplicationAdditionalInfo(final String oid, final Map<String, String> additionalInfo);

    void update(final Application application, boolean postProcess);

    void update(final Application queryApplication, final Application application);

    void update(Application queryApplication, Application application,
                boolean postProcess);

    List<String> massRedoPostProcess(List<String> applicationOids, Application.PostProcessingState newState);

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
     * Creates a new empty application to specified application system.
     *
     * @param asId application system id
     * @return created application
     */
    Application officerCreateNewApplication(final String asId);

    Application getSubmittedApplication(final String applicationSystemId, final String oid);

    List<Map<String, Object>> findApplicationsWithKeys(
            final ApplicationQueryParameters applicationQueryParameters,
            final String... keys);
    
    List<Map<String, Object>> findFullApplications(final ApplicationQueryParameters applicationQueryParameters);

    Map<String, Collection<Map<String, Object>>> findApplicationsByPersonOid(Set<String> personOids);

    Application updateAuthorizationMeta(Application application) throws IOException;

    Application updateAutomaticEligibilities(Application application);

    UpdatePreferenceResult updatePreferenceBasedData(final Application application);

    Application removeOrphanedAnswers(Application application) throws ValintaServiceCallFailedException ;

    Application getApplicationWithValintadata(Application application) throws ValintaServiceCallFailedException;
}
