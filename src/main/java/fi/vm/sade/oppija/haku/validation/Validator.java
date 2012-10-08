package fi.vm.sade.oppija.haku.validation;

import org.apache.commons.lang3.Validate;

import java.util.Map;

public abstract class Validator {

    public final String fieldName;
    public final String errorMessage;

    protected Validator(final String fieldName, final String errorMessage) {
        Validate.notNull(fieldName, "FieldName can't be null");
        Validate.notNull(errorMessage, "ErrorMessage can't be null");

        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

    public abstract ValidationResult validate(final Map<String, String> values);

}
