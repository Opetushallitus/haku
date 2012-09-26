package fi.vm.sade.oppija.haku.domain;

import fi.vm.sade.oppija.haku.validation.ValidationResult;

import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:48 PM}
 * @since 1.1
 */
public class Hakemus {
    private final HakemusId hakemusId;
    private final Map<String, String> values;
    private ValidationResult validationResult;


    public Hakemus(HakemusId id, Map<String, String> values) {
        this.hakemusId = id;
        this.values = values;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public HakemusId getHakemusId() {
        return hakemusId;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }
}
