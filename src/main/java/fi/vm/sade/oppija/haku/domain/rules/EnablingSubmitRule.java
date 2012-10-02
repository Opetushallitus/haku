package fi.vm.sade.oppija.haku.domain.rules;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 9/20/124:49 PM}
 * @since 1.1
 */
public class EnablingSubmitRule extends Element {
    private static final String RULE_PREFIX = "rule-enabled-";
    final Map<String, Element> related = new HashMap<String, Element>();

    public EnablingSubmitRule(@JsonProperty(value = "id") String id) {
        super(id);
    }

    public void setRelated(Element child, Element questionGroup) {
        this.related.put(child.getId(), questionGroup);
    }

    public Map<String, Element> getRelated() {
        return related;
    }
}
