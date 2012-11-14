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

package fi.vm.sade.oppija.haku.event;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author jukka
 * @version 10/12/125:40 PM}
 * @since 1.1
 */
@Service
public class SkipValidationEvent extends AbstractEvent {

    private final FormService formService;

    @Autowired
    public SkipValidationEvent(EventHandler eventHandler, @Qualifier("formServiceImpl") FormService formService) {
        this.formService = formService;
        eventHandler.addBeforeValidationEvent(this);
    }

    @Override
    public void process(HakemusState hakemusState) {
        Hakemus hakemus = hakemusState.getHakemus();
        if (hakemusState.getHakemus().getVastaukset().containsKey("enabling-submit")) {
            hakemusState.skipValidation();
            Form activeForm = formService.getActiveForm(hakemus.getHakemusId().getApplicationPeriodId(), hakemus.getHakemusId().getFormId());
            Vaihe vaihe = activeForm.getCategory(hakemus.getVaiheId());
            hakemusState.addModelObject("vaihe", vaihe);
        }
    }
}
