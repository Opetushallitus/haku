package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author jukka
 * @version 9/7/1210:30 AM}
 * @since 1.1
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class Attribute {
    String key;
    String value;

    public Attribute(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
