package fi.vm.sade.oppija.haku.model;

import java.util.Map;

/**
 * User: ville
 * Date: 05/09/12
 */
public class Link {

    private final String ref;
    private final String label;

    public Link(final Map<String, Object> map) {
        ref = map.get("id").toString();
        label = map.get("label").toString();
    }

    public String getRef() {
        return ref;
    }

    public String getLabel() {
        return label;
    }
}
