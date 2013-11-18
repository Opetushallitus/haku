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
    @Transient
    private final Predicate<Map.Entry<String, String>> notNullKeyValue;

    @PersistenceConstructor
    public UniqValuesValidator(@JsonProperty(value = "fieldName") final String fieldName,
                               @JsonProperty(value = "keys") final List<String> keys,
                               @JsonProperty(value = "errorMessage") final I18nText errorMessage) {
        super(fieldName, errorMessage);
        Preconditions.checkNotNull(fieldName);
        Preconditions.checkNotNull(keys);
        Preconditions.checkNotNull(errorMessage);
        this.keys = ImmutableList.copyOf(keys);
        this.notNullKeyValue = new NotNullKeyValues(keys);
    }

    @Override
    public ValidationResult validate(final ValidationInput validationInput) {
        Collection<String> values = Maps.filterEntries(validationInput.getValues(), notNullKeyValue).values();
        Set<String> uniqValues = new HashSet<String>(values);
        if (uniqValues.size() != values.size()) {
            return invalidValidationResult;
        }
        return new ValidationResult();
    }

    public List<String> getKeys() {
        return keys;
    }

    private final class NotNullKeyValues implements Predicate<Map.Entry<String, String>> {
        private final List<String> keys = new ArrayList<String>();

        private NotNullKeyValues(List<String> keys) {
            this.keys.addAll(keys);
        }

        @Override
        public boolean apply(Map.Entry<String, String> entry) {
            return entry.getValue() != null && keys.contains(entry.getKey());

        }
    }


}
