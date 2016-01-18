package fi.vm.sade.haku.oppija.lomake.validation.validators;

import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;

public class EmailInLowercaseValidator implements Validator {

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        return new ValidationResult();
    }
}
