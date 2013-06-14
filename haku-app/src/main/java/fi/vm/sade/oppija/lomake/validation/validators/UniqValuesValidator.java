package fi.vm.sade.oppija.lomake.validation.validators;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomake.validation.Validator;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;

import java.util.*;

public class UniqValuesValidator implements Validator {

    private final Predicate<Map.Entry<String, String>> notNullKeyValue;
    private final String id;
    private final String errorMessageKey;

    public UniqValuesValidator(final String id, final List<String> keys, final String messageKey) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(keys);
        Preconditions.checkNotNull(messageKey);
        this.notNullKeyValue = new NotNullKeyValues(keys);
        this.id = id;
        this.errorMessageKey = messageKey;
    }

    @Override
    public ValidationResult validate(Map<String, String> allValues) {
        Collection<String> values = Maps.filterEntries(allValues, notNullKeyValue).values();
        Set<String> uniqValues = new HashSet<String>(values);
        if (uniqValues.size() != values.size()) {
            return new ValidationResult(id, ElementUtil.createI18NTextError(errorMessageKey));
        }
        return new ValidationResult();
    }

    private class NotNullKeyValues implements Predicate<Map.Entry<String, String>> {
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
