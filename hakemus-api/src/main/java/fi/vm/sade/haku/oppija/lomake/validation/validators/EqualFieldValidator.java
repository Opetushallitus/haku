package fi.vm.sade.haku.oppija.lomake.validation.validators;

import fi.vm.sade.haku.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.apache.commons.lang.StringUtils;

public class EqualFieldValidator extends FieldValidator {

    final String otherFieldName;

    public EqualFieldValidator(final String otherFieldName, final String errorMessageKey) {
        super(errorMessageKey);
        this.otherFieldName = otherFieldName;
    }

    public ValidationResult validate(final ValidationInput validationInput) {
        String otherValue = StringUtils.trim(validationInput.getValueByKey(otherFieldName));
        String thisValue = StringUtils.trim(validationInput.getValue());
        if(otherValue == thisValue){
            return validValidationResult;
        }
        if(otherValue.equalsIgnoreCase(thisValue)){
            return validValidationResult;
        }
        return getInvalidValidationResult(validationInput);
    }
}
