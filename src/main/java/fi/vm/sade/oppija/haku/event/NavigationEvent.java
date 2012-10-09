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
import fi.vm.sade.oppija.haku.domain.elements.Category;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        Category category = getNextCategory(hakemusState);
        hakemusState.addModelObject("category", category);
    }

    private Category getNextCategory(HakemusState hakemusState) {
        Hakemus hakemus = hakemusState.getHakemus();
        Form activeForm = formService.getActiveForm(hakemus.getHakemusId().getApplicationPeriodId(), hakemus.getHakemusId().getFormId());

        Category category = activeForm.getCategory(hakemus.getHakemusId().getCategoryId());
        if (hakemusState.isValid()) {
            category = selectNextPrevOrCurrent(hakemus.getValues(), category);
        }
        return category;
    }

    private Category selectNextPrevOrCurrent(Map<String, String> values, Category category) {
        if (values.get("nav-next") != null && category.isHasNext()) {
            return category.getNext();
        } else if (values.get("nav-prev") != null && category.isHasPrev()) {
            return category.getPrev();
        }
        return category;
    }
}
