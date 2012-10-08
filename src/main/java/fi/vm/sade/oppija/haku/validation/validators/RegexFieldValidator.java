package fi.vm.sade.oppija.haku.validation.validators;

import fi.vm.sade.oppija.haku.validation.ValidationResult;
import fi.vm.sade.oppija.haku.validation.Validator;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexFieldValidator extends Validator {

    final Pattern pattern;

    public RegexFieldValidator(final String fieldName, final String pattern) {
        this(fieldName, "Virheellinen syöte " + pattern, pattern);
    }

    public RegexFieldValidator(final String fieldName, final String errorMessage, final String pattern) {
        super(fieldName, errorMessage);
        Validate.notNull(pattern, "Pattern can't be null");
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public ValidationResult validate(Map<String, String> values) {
        Matcher matcher = pattern.matcher(values.get(fieldName));
        ValidationResult validationResult = new ValidationResult();
        if (!matcher.matches()) {
            validationResult = new ValidationResult(fieldName, errorMessage);
        }
        return validationResult;
    }
}
