package fi.vm.sade.oppija.haku.domain.rules;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Element;

/**
 * @author jukka
 * @version 9/20/124:49 PM}
 * @since 1.1
 */
public class EnablingSubmitRule extends Element {

    private final String expression;

    public EnablingSubmitRule(@JsonProperty(value = "id") String id, @JsonProperty(value = "expression") String expression) {
        super(id);
        this.expression = expression;
    }

    public void setRelated(Element option, Element target) {
        getChildById().put(option.getId(), target);
        children.add(target);
    }

    public String getExpression() {
        return expression;
    }
}
