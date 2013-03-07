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

import fi.vm.sade.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;

import java.util.Map;

public class ContainedInOtherFieldValidator extends FieldValidator {

    final String otherFieldName;

    public ContainedInOtherFieldValidator(final String fieldName, final String otherFieldName) {
        super(fieldName, "Virheellinen syöte");
        this.otherFieldName = otherFieldName;
    }

    @Override
    public ValidationResult validate(Map<String, String> values) {
        String otherValue = values.get(otherFieldName);
        String thisValue = values.get(fieldName);
        if (otherValue == null || thisValue == null || !otherValue.contains(thisValue)) {
            return invalidValidationResult;
        }
        return validValidationResult;
    }

}
