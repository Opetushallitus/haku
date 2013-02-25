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

package fi.vm.sade.oppija.hakemus.domain;

import fi.vm.sade.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.oppija.lomake.domain.elements.Form;

/**
 * @author jukka
 * @version 10/17/1212:50 PM}
 * @since 1.1
 */
public class ApplicationInfo {
    public static final String STATE_PENDING = "valmis";
    final Application application;
    final Form form;
    final ApplicationPeriod applicationPeriod;


    public ApplicationInfo(final Application application, final Form form, final ApplicationPeriod applicationPeriod) {
        this.application = application;
        this.form = form;
        this.applicationPeriod = applicationPeriod;
    }

    public Application getApplication() {
        return application;
    }

    public Form getForm() {
        return form;
    }

    public ApplicationPeriod getApplicationPeriod() {
        return applicationPeriod;
    }

    public boolean isPending() {
        return STATE_PENDING.equals(this.application.getPhaseId());
    }

}
