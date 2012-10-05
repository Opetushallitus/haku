package fi.vm.sade.oppija.haku.domain.rules;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.questions.Option;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 10/3/123:25 PM}
 * @since 1.1
 */
public class SelectingSubmitRule extends Rule {
    private final Map<String, RegexOptionRule> expressions = new HashMap<String, RegexOptionRule>();
    private final String target;

    public SelectingSubmitRule(@JsonProperty(value = "id") String id, @JsonProperty(value = "target") String target) {
        super(id);
        this.target = target;
    }


    public void addBinding(Element parent, Element child, String s, Option option) {
        getChildById().put(getId(), parent);
        getChildById().put(target, child);
        expressions.put(option.getId(), new RegexOptionRule(s, option));
    }

    public Map<String, RegexOptionRule> getExpressions() {
        return expressions;
    }

    public String getTarget() {
        return target;
    }
}
