package fi.vm.sade.oppija.haku.domain.rules;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.questions.Option;

/**
 * @author jukka
 * @version 10/3/123:25 PM}
 * @since 1.1
 */
public class SelectingSubmitRule extends EnablingSubmitRule {
    private final Option option;

    public SelectingSubmitRule(@JsonProperty(value = "id") String id, @JsonProperty(value = "expression") String expression, @JsonProperty(value = "option") Option option) {
        super(id, expression);
        this.option = option;
    }

    public Option getOption() {
        return option;
    }
}
