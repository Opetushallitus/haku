package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.validation.Validator;
import org.apache.commons.lang3.Validate;

import java.util.*;

public class ValidatorContainer {

    final Map<String, List<Validator>> categoryValidators = new HashMap<String, List<Validator>>();

    public void addValidator(final String category, final Validator validator) {
        Validate.notNull(category, "category can't be null");
        List<Validator> validators = this.categoryValidators.get(category);
        if (validators == null) {
            validators = new ArrayList<Validator>();
            this.categoryValidators.put(category, validators);
        }
        validators.add(validator);
    }

    public void addValidator(final String currentCategory, final List<Validator> validators) {
        for (Validator validator : validators) {
            addValidator(currentCategory, validator);
        }
    }

    public List<Validator> getCategoryValidators(final String category) {
        List<Validator> list = categoryValidators.get(category);
        if (list == null) {
            return Collections.unmodifiableList(new ArrayList<Validator>());
        }
        return Collections.unmodifiableList(list);
    }
}
