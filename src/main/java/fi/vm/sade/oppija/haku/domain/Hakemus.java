package fi.vm.sade.oppija.haku.domain;

import fi.vm.sade.oppija.haku.validation.HakemusState;

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
    private HakemusState hakemusState;


    public Hakemus(HakemusId id, Map<String, String> values) {
        this.hakemusId = id;
        this.values = values;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public HakemusState getHakemusState() {
        return hakemusState;
    }

    public HakemusId getHakemusId() {
        return hakemusId;
    }

    public void setHakemusState(HakemusState hakemusState) {
        this.hakemusState = hakemusState;
    }
}
