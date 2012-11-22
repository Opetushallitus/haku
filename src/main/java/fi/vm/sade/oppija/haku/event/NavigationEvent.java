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

/**
 * @author jukka
 * @version 9/28/122:06 PM}
 * @since 1.1
 */
public class NavigationEvent implements Event {

    private FormService formService;

    @Autowired
    public NavigationEvent(@Qualifier("formServiceImpl") FormService formService) {
        this.formService = formService;
    }

    @Override
    public void process(HakemusState hakemusState) {
        setNextCategoryIfValid(hakemusState);
    }

    private void setNextCategoryIfValid(HakemusState hakemusState) {
        if (hakemusState.isValid()) {
            Hakemus hakemus = hakemusState.getHakemus();
            Form activeForm = formService.getActiveForm(hakemus.getHakuLomakeId().getApplicationPeriodId(), hakemus.getHakuLomakeId().getFormId());
            Vaihe vaihe = activeForm.getCategory(hakemusState.getVaiheId());
            vaihe = selectNextPrevOrCurrent(hakemusState, vaihe);
            hakemusState.setVaiheId(vaihe.getId());
        }
    }

    private Vaihe selectNextPrevOrCurrent(HakemusState hakemusState, Vaihe vaihe) {
        if (hakemusState.isNavigateNext() && vaihe.isHasNext()) {
            return vaihe.getNext();
        } else if (hakemusState.isNavigatePrev() && vaihe.isHasPrev()) {
            return vaihe.getPrev();
        }
        return vaihe;
    }
}
