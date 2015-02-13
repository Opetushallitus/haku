package fi.vm.sade.haku.oppija.lomake.validation.validators;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.validation.FieldValidator;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;

import java.util.*;

public final class UniqValuesValidator extends FieldValidator {

    private final List<String> keys;
    private final List<String> skipValues;
    @Transient
    private final Predicate<Map.Entry<String, String>> valuePredicate;

    @PersistenceConstructor
    public UniqValuesValidator(@JsonProperty(value = "keys") final List<String> keys,
                               @JsonProperty(value = "skipValues") final List<String> skipValues,
                               @JsonProperty(value = "errorMessage") final String errorMessageKey) {
        super(errorMessageKey);
        Preconditions.checkNotNull(keys);
        Preconditions.checkNotNull(skipValues);
        Preconditions.checkNotNull(errorMessageKey);
        this.keys = ImmutableList.copyOf(keys);
        this.skipValues = ImmutableList.copyOf(skipValues);
        this.valuePredicate = new Predicate<Map.Entry<String, String>>() {
            @Override
            public boolean apply(Map.Entry<String, String> entry) {
                String value = entry.getValue();
                return value != null && keys.contains(entry.getKey()) && !skipValues.contains(value);
            }
        };
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        Collection<String> values = Maps.filterEntries(validationInput.getValues(), valuePredicate).values();
        Set<String> uniqValues = new HashSet<String>(values);
        if (uniqValues.size() != values.size()) {
            return getInvalidValidationResult(validationInput);
        }
        return new ValidationResult();
    }
}
