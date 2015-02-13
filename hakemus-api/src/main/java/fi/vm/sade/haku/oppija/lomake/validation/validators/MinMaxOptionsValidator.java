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

package fi.vm.sade.haku.oppija.lomake.validation.validators;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.TitledGroup;
import fi.vm.sade.haku.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;

public class MinMaxOptionsValidator extends FieldValidator {

    private final String id;
    private final int min;
    private final int max;

    public MinMaxOptionsValidator(final String errorMessageKey, final Integer min, final Integer max) {
        super(errorMessageKey);
        if (min == null || min.intValue() < 0) {
            throw new IllegalArgumentException("Minimum must be non null and non negative");
        }
        if (max == null || max.intValue() < 1) {
            throw new IllegalArgumentException("Maximum must be non null and positive");
        }
        id = null;
        this.min = min.intValue();
        this.max = max.intValue();
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        if (validationInput.getElement().getType().equals(TitledGroup.class.getSimpleName())) {
            int valueCount = 0;
            for (Element child : validationInput.getElement().getChildren()) {
                if (hasValue(validationInput, child.getId())) {
                    valueCount++;
                }
            }
            if (valueCount >= min && valueCount <= max) {
                return validValidationResult;
            }
        }
        return getInvalidValidationResult(validationInput);
    }

    private boolean hasValue(final ValidationInput validationInput, final String id) {
        String value;
        if (id != null) {
            value = validationInput.getValueByKey(id);
        } else {
            value = validationInput.getValue();
        }
        return !StringUtils.isBlank(value);
    }
}
