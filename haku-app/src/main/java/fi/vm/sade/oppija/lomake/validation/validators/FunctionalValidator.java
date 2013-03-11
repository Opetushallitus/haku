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

import com.google.common.base.Predicate;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomake.validation.Validator;

import java.util.Map;

/**
 * @author Mikko Majapuro
 */
public class FunctionalValidator implements Validator {

    private Predicate<Map<String, String>> predicate;
    private String inputId;
    private String errorMessage;

    public FunctionalValidator(Predicate<Map<String, String>> predicate, final String inputId,
                               final String errorMessage) {
        this.predicate = predicate;
        this.inputId = inputId;
        this.errorMessage = errorMessage;
    }

    @Override
    public ValidationResult validate(Map<String, String> values) {

        if (this.predicate.apply(values)) {
            return new ValidationResult();
        }
        return new ValidationResult(inputId, errorMessage);
    }

    public static final class ValidatorPredicate implements Predicate<Map<String, String>> {

        private Validator validator;

        private ValidatorPredicate(Validator validator) {
            this.validator = validator;
        }

        public static Predicate<Map<String, String>> validate(Validator validator) {
            return new ValidatorPredicate(validator);
        }

        @Override
        public boolean apply(Map<String, String> stringStringMap) {
            return !validator.validate(stringStringMap).hasErrors();
        }
    }
}
