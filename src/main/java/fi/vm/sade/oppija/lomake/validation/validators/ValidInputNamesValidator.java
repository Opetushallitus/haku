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

package fi.vm.sade.oppija.lomake.validation.validators;

import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomake.validation.Validator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ValidInputNamesValidator implements Validator {
    final Set<String> validValues = new HashSet<String>();

    public ValidInputNamesValidator(final Set<String> validNames) {
        validValues.addAll(validNames);
    }

    @Override
    public ValidationResult validate(final Map<String, String> values) {
        for (String key : values.keySet()) {
            if (!validValues.contains(key)) {
                throw new RuntimeException("Unknown parameter");
            }
        }
        return new ValidationResult();
    }
}
