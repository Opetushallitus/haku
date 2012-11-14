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
 * @version 9/28/122:06 PM}
 * @since 1.1
 */
@Service
public class NavigationEvent extends AbstractEvent {

    private FormService formService;

    @Autowired
    public NavigationEvent(EventHandler eventHandler, @Qualifier("formServiceImpl") FormService formService) {
        this.formService = formService;
        eventHandler.addPostValidateEvent(this);
    }

    @Override
    public void process(HakemusState hakemusState) {
        Vaihe vaihe = getNextCategory(hakemusState);
        hakemusState.addModelObject("vaihe", vaihe);
    }

    private Vaihe getNextCategory(HakemusState hakemusState) {
        Hakemus hakemus = hakemusState.getHakemus();
        Form activeForm = formService.getActiveForm(hakemus.getHakemusId().getApplicationPeriodId(), hakemus.getHakemusId().getFormId());

        Vaihe vaihe = activeForm.getCategory(hakemus.getVaiheId());
        if (hakemusState.isValid()) {
            vaihe = selectNextPrevOrCurrent(hakemusState, vaihe);
        }
        return vaihe;
    }

    private Vaihe selectNextPrevOrCurrent(HakemusState values, Vaihe vaihe) {
        if (values.isNavigateNext() && vaihe.isHasNext()) {
            return vaihe.getNext();
        } else if (values.isNavigatePrev() && vaihe.isHasPrev()) {
            return vaihe.getPrev();
        }
        return vaihe;
    }
}
