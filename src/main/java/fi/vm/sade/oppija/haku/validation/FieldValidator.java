package fi.vm.sade.oppija.haku.validation;

public abstract class FieldValidator extends Validator {

    public final String fieldName;

    protected FieldValidator(String errorMessage, String fieldName) {
        super(errorMessage);
        this.fieldName = fieldName;
    }

}
