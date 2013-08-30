/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.oppija.ui.service;

import fi.vm.sade.oppija.lomake.domain.exception.ResourceNotFoundException;

import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public interface UIService {

    /**
     * OFFICER USE ONLY
     * Builds the model of the officer application print view.
     *
     * @param oid application oid
     * @return ui service response containing model data
     * @throws ResourceNotFoundException
     */
    UIServiceResponse getApplicationPrint(final String oid) throws ResourceNotFoundException;

    /**
     * Can be used to build the model of the user application sent/complete print view
     *
     * @param applicationSystemId
     * @param oid
     * @return
     * @throws ResourceNotFoundException
     */
    UIServiceResponse getApplicationPrint(final String applicationSystemId, final String oid) throws ResourceNotFoundException;

    UIServiceResponse getApplicationComplete(final String applicationSystemId, final String oid) throws ResourceNotFoundException;

    Map<String, Object> getElementHelp(final String applicationSystemId, final String elementId) throws ResourceNotFoundException;
}
