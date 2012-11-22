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

package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import fi.vm.sade.oppija.haku.validation.Validator;

import java.util.List;
import java.util.Map;

public interface FormService {

    Form getActiveForm(final String applicationPeriodId, final String formId);

    Vaihe getFirstCategory(final String applicationPeriodId, final String formId);

    Map<String, ApplicationPeriod> getApplicationPerioidMap();

    ApplicationPeriod getApplicationPeriodById(final String applicationPeriodId);


    Form getForm(String applicationPeriodId, String formId);

    List<Validator> getVaiheValidators(HakemusState hakemusState);
}
