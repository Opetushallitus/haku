package fi.vm.sade.oppija.haku.validation;

import java.util.Map;

public abstract class Validator {

    public final String errorMessage;

    protected Validator(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public abstract boolean validate(final Map<String, String> values);
}
