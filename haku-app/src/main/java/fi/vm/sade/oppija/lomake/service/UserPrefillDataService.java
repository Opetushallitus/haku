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
package fi.vm.sade.oppija.lomake.service;

import java.util.Map;

/**
 * Service to get user prefill data.
 *
 * @author Mikko Majapuro
 */
public interface UserPrefillDataService {

    /**
     * Get all the user prefill data
     *
     * @return map of prefill data
     */
    Map<String, String> getUserPrefillData();

    /**
     * Populates data with user prefill data.
     * If key value pair is already found, method does not overwrite it.
     *
     * @param data data to be populated with prefill data
     * @return populated data
     */
    Map<String, String> populateWithPrefillData(Map<String, String> data);

    /**
     * Adds user prefill data
     * Overwrites values of the existing keys
     *
     * @param data data to be added to the user prefill data
     */
    void addUserPrefillData(Map<String, String> data);
}
