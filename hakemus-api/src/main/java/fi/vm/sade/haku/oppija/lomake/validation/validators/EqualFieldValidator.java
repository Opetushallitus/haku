package fi.vm.sade.haku.oppija.lomake.validation.validators;

import com.google.common.base.Strings;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.FieldValidator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class EqualFieldValidator extends FieldValidator {

    final String otherFieldName;
    final String errorMessageKey;

    public EqualFieldValidator(final String otherFieldName, final String errorMessageKey) {
        super(errorMessageKey);
        this.errorMessageKey = errorMessageKey;
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
