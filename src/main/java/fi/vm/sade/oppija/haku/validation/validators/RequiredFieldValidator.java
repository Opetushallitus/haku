package fi.vm.sade.oppija.haku.validation.validators;

import fi.vm.sade.oppija.haku.validation.Validator;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class RequiredFieldValidator extends Validator {

    public final String fieldName;

    public RequiredFieldValidator(String errorMessage, String fieldName) {
        super(errorMessage);
        this.fieldName = fieldName;
    }

    @Override
    public boolean validate(final Map<String, String> values) {
        return (values != null && StringUtils.isNotBlank(values.get(fieldName)));
    }
}
