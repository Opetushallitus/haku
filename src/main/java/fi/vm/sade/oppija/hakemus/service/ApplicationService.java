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

import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationInfo;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.oppija.lomake.domain.FormId;
import fi.vm.sade.oppija.lomake.validation.ApplicationState;

import java.util.List;

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
     * @return
     */
    Application getApplication(String oid);

    String submitApplication(final FormId formId);

    Application getPendingApplication(final FormId hakuLomakeId, final String oid);
}
