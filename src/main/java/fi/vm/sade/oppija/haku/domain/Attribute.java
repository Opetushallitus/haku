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
    String key;
    String value;

    public Attribute(@JsonProperty(value = "key") String key, @JsonProperty(value = "value") String value) {
        this.key = key.toString();
        this.value = value.toString();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
