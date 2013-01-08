/*
 *
 *  * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *  *
 *  * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 *  * soon as they will be approved by the European Commission - subsequent versions
 *  * of the EUPL (the "Licence");
 *  *
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * European Union Public Licence for more details.
 *
 */

package fi.vm.sade.oppija.application.process.service;

import fi.vm.sade.oppija.application.process.domain.ApplicationProcessState;
import fi.vm.sade.oppija.application.process.domain.ApplicationProcessStateStatus;

/**
 * @author Mikko Majapuro
 */
public interface ApplicationProcessStateService {

    /**
     * Sets process status to application, if status does not exists, it creates a new
     * @param oid Application oid
     * @param status process status
     */
    void setApplicationProcessStateStatus(final String oid, final ApplicationProcessStateStatus status);

    /**
     * Gets application process state by application oid
     * @param oid oid of application
     * @return application process state
     */
    ApplicationProcessState get(final String oid);
}
