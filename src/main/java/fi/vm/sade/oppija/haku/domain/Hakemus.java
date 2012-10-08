package fi.vm.sade.oppija.haku.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * @author jukka
 * @version 9/26/122:48 PM}
 * @since 1.1
 */
public class Hakemus implements Serializable {
    private final HakemusId hakemusId;
    private final Map<String, String> values;


    public Hakemus(HakemusId id, Map<String, String> values) {
        this.hakemusId = id;
        this.values = values;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public HakemusId getHakemusId() {
        return hakemusId;
    }
}
