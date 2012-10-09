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

package fi.vm.sade.oppija.haku.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FormValidator {
    private FormValidator() {
    }

    public static ValidationResult validate(final List<Validator> validators, final Map<String, String> values) {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        for (Validator validator : validators) {
            validationResults.add(validator.validate(values));
        }
        return new ValidationResult(validationResults);
    }


}
