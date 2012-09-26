package fi.vm.sade.oppija.haku.domain.rules;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Element;

import java.util.HashMap;

/**
 * @author jukka
 * @version 9/20/124:49 PM}
 * @since 1.1
 */
public class EnablingSubmitRule extends Element {
    final HashMap<String, Element> related = new HashMap<String, Element>();

    public EnablingSubmitRule(@JsonProperty String id) {
        super("rule-enabled-" + id);
    }

    public EnablingSubmitRule() {
        super("" + System.currentTimeMillis());
    }

    public void setRelated(Element child, Element questionGroup) {
        this.related.put("rule-enabled-" + child.getId(), questionGroup);
    }


    public HashMap<String, Element> getRelated() {
        return related;
    }
}
