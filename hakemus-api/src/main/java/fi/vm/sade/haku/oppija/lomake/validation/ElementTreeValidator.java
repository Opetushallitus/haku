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

package fi.vm.sade.haku.oppija.lomake.validation;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ElementTreeValidator {

    private final ValidatorFactory validatorFactory;

    @Autowired
    public ElementTreeValidator(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    public ValidationResult validate(final ValidationInput validationInput) {
        List<ValidationResult> listOfValidationResult = new ArrayList<ValidationResult>();
        Element element = validationInput.getElement();
        for (Validator validator : element.getValidators()) {
            listOfValidationResult.add(validatorFactory.buildValidator(validator).validate(validationInput));
        }
        for (Element child : element.getChildren(validationInput.getValues())) {
            listOfValidationResult.add(validate(new ValidationInput(child, validationInput)));
        }
        return new ValidationResult(listOfValidationResult);
    }
}
