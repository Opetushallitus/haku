package fi.vm.sade.oppija.haku.validation.validators;

import fi.vm.sade.oppija.haku.validation.ValidationResult;
import fi.vm.sade.oppija.haku.validation.Validator;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class RequiredFieldValidator extends Validator {


    public RequiredFieldValidator(String fieldName) {
        super(fieldName, fieldName + " on pakollinen kenttä");
    }

    public RequiredFieldValidator(final String fieldName, final String errorMessage) {
        super(fieldName, errorMessage);
    }

    @Override
    public ValidationResult validate(final Map<String, String> values) {
        ValidationResult validationResult = new ValidationResult();
        if (values == null || StringUtils.isBlank(values.get(fieldName))) {
            validationResult = new ValidationResult(fieldName, errorMessage);
        }
        return validationResult;
    }
}
