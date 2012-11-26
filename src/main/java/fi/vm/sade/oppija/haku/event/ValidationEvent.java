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
import fi.vm.sade.oppija.haku.service.FormService;
import fi.vm.sade.oppija.haku.validation.FormValidator;
import fi.vm.sade.oppija.haku.validation.HakemusState;
import fi.vm.sade.oppija.haku.validation.ValidationResult;
import fi.vm.sade.oppija.haku.validation.Validator;
import fi.vm.sade.oppija.haku.validation.validators.ValidInputNamesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author jukka
 * @version 9/28/122:42 PM}
 * @since 1.1
 */
public class ValidationEvent implements Event {

    protected final FormService formService;

    @Autowired
    public ValidationEvent(@Qualifier("formServiceImpl") FormService formService) {
        this.formService = formService;
    }

    @Override
    public void process(HakemusState hakemusState) {
        Hakemus hakemus = hakemusState.getHakemus();
        List<Validator> validators = getValidators(hakemusState);
        //validators.addAll(getTestValidators());
        ValidationResult validationResult = FormValidator.validate(validators, hakemus.getVastauksetMerged());
        hakemusState.addError(validationResult.getErrorMessages());
    }


    protected List<Validator> getValidators(HakemusState hakemusState) {
        return formService.getVaiheValidators(hakemusState);
    }

    public Collection<? extends Validator> getTestValidators() {
        List<Validator> listOfValidators = new ArrayList<Validator>(1);
        HashSet<String> keys = new HashSet<String>();
        keys.add("etunimi");
        ValidInputNamesValidator validator = new ValidInputNamesValidator(keys);
        listOfValidators.add(validator);
        return listOfValidators;
    }
}
