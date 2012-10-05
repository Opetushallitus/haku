package fi.vm.sade.oppija.haku.domain.rules;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jukka
 * @version 10/4/123:31 PM}
 * @since 1.1
 */
public class Rule extends Element {
    protected Map<String, Element> childById = new HashMap<String, Element>();

    protected Rule(@JsonProperty String id) {
        super(id);
    }

    public Map<String, Element> getChildById() {
        return childById;
    }

}
