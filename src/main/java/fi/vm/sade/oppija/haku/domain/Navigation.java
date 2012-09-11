package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/1210:50 AM}
 * @since 1.1
 */
public class Navigation extends Element {

    public Navigation(@JsonProperty(value = "id") String id) {
        super(id);
    }


}
