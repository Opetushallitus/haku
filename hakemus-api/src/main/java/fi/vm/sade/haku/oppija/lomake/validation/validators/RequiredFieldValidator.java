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
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Collections;
import java.util.List;

public class RequiredFieldValidator extends FieldValidator {

    private final String id;
    private List<ValidationInput.ValidationContext> context;

    @PersistenceConstructor
    public RequiredFieldValidator(final String errorMessageKey) {
        super(errorMessageKey);
        id = null;
    }

    public RequiredFieldValidator(final String errorMessageKey, List<ValidationInput.ValidationContext> context) {
        super(errorMessageKey);
        id = null;
        this.context = Collections.unmodifiableList(context);
    }

    public RequiredFieldValidator(final String id, final String errorMessageKey) {
        super(errorMessageKey);
        this.id = id;
    }

    public RequiredFieldValidator(final String id, final String errorMessageKey, List<ValidationInput.ValidationContext> context) {
        super(errorMessageKey);
        this.context = Collections.unmodifiableList(context);
        this.id = id;
    }

    public List<ValidationInput.ValidationContext> getContext() {
        return context;
    }

    public void setContext(List<ValidationInput.ValidationContext> context) {
        this.context = context;
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        if (!inContext(validationInput)) {
            return validValidationResult;
        }
        if (validationInput.getElement().getType().equals(TitledGroup.class.getSimpleName())) {
            for (Element child : validationInput.getElement().getChildren()) {
                if (hasValue(validationInput, child.getId())) {
                    return validValidationResult;
                }
            }
        } else if (hasValue(validationInput, this.id)) {
            return validValidationResult;
        }
        return getInvalidValidationResult(validationInput);
    }

    private boolean inContext(ValidationInput validationInput) {
        if (context == null || context.isEmpty()) {
            return true;
        }
        for (ValidationInput.ValidationContext c : context) {
            if (c.equals(validationInput.getValidationContext())) {
                return true;
            }
        }
        return validationInput.getValidationContext() == null;
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
