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
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomake.validation.Validator;
import org.apache.commons.lang3.Validate;

public class FunctionalValidator implements Validator {

    private Predicate<ValidationInput> predicate;
    private String inputId;
    private I18nText errorMessage;

    public FunctionalValidator(Predicate<ValidationInput> predicate, final String inputId,
                               final I18nText errorMessage) {
        Validate.notNull(predicate, "'predicate' can't be null");
        Validate.notNull(inputId, "'inputId' can't be null");
        Validate.notNull(errorMessage, "'errorMessage' can't be null");
        this.predicate = predicate;
        this.inputId = inputId;
        this.errorMessage = errorMessage;
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {

        if (this.predicate.apply(validationInput)) {
            return new ValidationResult();
        }
        return new ValidationResult(inputId, errorMessage);
    }

    public static final class ValidatorPredicate implements Predicate<ValidationInput> {

        private Validator validator;

        private ValidatorPredicate(Validator validator) {
            this.validator = validator;
        }

        public static Predicate<ValidationInput> validate(Validator validator) {
            return new ValidatorPredicate(validator);
        }

        @Override
        public boolean apply(ValidationInput validationInput) {
            return !validator.validate(validationInput).hasErrors();
        }
    }
}
