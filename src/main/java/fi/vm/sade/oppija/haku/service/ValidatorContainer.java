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

import fi.vm.sade.oppija.haku.validation.Validator;
import org.apache.commons.lang3.Validate;

import java.util.*;

public class ValidatorContainer {

    private final Map<String, List<Validator>> categoryValidators = new HashMap<String, List<Validator>>();

    public void addValidator(final String category, final Validator validator) {
        Validate.notNull(category, "category can't be null");
        List<Validator> validators = this.categoryValidators.get(category);
        if (validators == null) {
            validators = new ArrayList<Validator>();
            this.categoryValidators.put(category, validators);
        }
        validators.add(validator);
    }

    public void addValidator(final String currentCategory, final List<Validator> validators) {
        for (Validator validator : validators) {
            addValidator(currentCategory, validator);
        }
    }

    public List<Validator> getCategoryValidators(final String category) {
        List<Validator> list = categoryValidators.get(category);
        if (list == null) {
            return Collections.unmodifiableList(new ArrayList<Validator>());
        }
        return Collections.unmodifiableList(list);
    }
}
