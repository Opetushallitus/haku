package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * @author jukka
 * @version 9/7/1210:30 AM}
 * @since 1.1
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Attribute implements Serializable {

    private static final long serialVersionUID = 3012830534204489765L;

    private final String key;
    private final String value;

    public Attribute(final @JsonProperty(value = "key") String key, final @JsonProperty(value = "value") String value) {
        assert key != null;
        assert value != null;
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
