package fi.vm.sade.oppija.haku.validation.validators;

import fi.vm.sade.oppija.haku.validation.FieldValidator;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpFieldValidator extends FieldValidator {

    final Pattern pattern;

    public RegexpFieldValidator(final String errorMessage, final String fieldName, final String pattern) {
        super(errorMessage, fieldName);
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean validate(Map<String, String> values) {
        Matcher matcher = pattern.matcher(values.get(fieldName));
        return matcher.matches();
    }
}
