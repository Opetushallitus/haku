/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.oppija.lomake.validation;

import fi.vm.sade.oppija.lomake.validation.validators.SsnUniqueValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Mikko Majapuro
 */
@Component
public class ValidatorFactory {

    private final SsnUniqueConcreteValidator ssnUniqueConcreteValidator;

    @Autowired
    public ValidatorFactory(SsnUniqueConcreteValidator ssnUniqueConcreteValidator) {
        this.ssnUniqueConcreteValidator = ssnUniqueConcreteValidator;
    }

    public Validator buildValidator(final Validator validator) {
        if (validator != null && validator instanceof SsnUniqueValidator) {
            return ssnUniqueConcreteValidator;
        }
        return validator;
    }
}
