package fi.vm.sade.oppija.haku.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:48 PM}
 * @since 1.1
 */
public class Hakemus implements Serializable {

    private static final long serialVersionUID = -7491168801255850954L;

    private final HakemusId hakemusId;
    private final Map<String, String> values;


    public Hakemus(final HakemusId id, final Map<String, String> values) {
        this.hakemusId = id;
        this.values = Collections.unmodifiableMap(values);
    }

    public Map<String, String> getValues() {
        return values;
    }

    public HakemusId getHakemusId() {
        return hakemusId;
    }
}
