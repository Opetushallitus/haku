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

import fi.vm.sade.haku.oppija.common.dao.BaseDAO;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationAdditionalDataDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;

import java.util.List;
import java.util.Map;

/**
 * DAO interface for saving, updating and finding applications made by users.
 *
 * @author Hannu Lyytikainen
 */
public interface ApplicationDAO extends BaseDAO<Application> {

    /**
     * Return list of applications that match given model application, state and
     *
     * @param application
     * @return
     */
    List<Application> find(Application application);

    Application getApplication(String oid, String... fields);

    List<ApplicationAdditionalDataDTO> findApplicationAdditionalData(String applicationSystemId, String aoId,
                                                                     ApplicationFilterParameters filterParameters);
    List<ApplicationAdditionalDataDTO> findApplicationAdditionalData(List<String> oids,
                                                                     ApplicationFilterParameters filterParameters);
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
    boolean checkIfExistsBySocialSecurityNumberAndAo(ApplicationFilterParameters filterParameters,
                                                     String asId, String ssn, String aoId);

    ApplicationSearchResultDTO findAllQueried(ApplicationQueryParameters queryParameters,
                                              ApplicationFilterParameters filterParameters);

    List<Map<String, Object>> findAllQueriedFull(ApplicationQueryParameters queryParameters,
                                                 ApplicationFilterParameters filterParameters);

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

    Application getNextRedo();

    List<String> massRedoPostProcess(List<String> applicationOids, Application.PostProcessingState state);

    List<Application> getNextUpgradable(int versionLevel, int batchSize);

    void updateModelVersion(Application application, int modelVersion);

    boolean hasApplicationsWithModelVersion(int versionLevel);

    List<Application> getApplicationsByPersonOid(List<String> personOids);

}
