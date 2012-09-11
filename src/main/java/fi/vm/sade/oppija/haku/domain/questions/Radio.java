package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/122:04 PM}
 * @since 1.1
 */
public class Radio extends OptionQuestion {

    public Radio(@JsonProperty(value = "id") final String id, final String title, final String name) {
        super(id, title, name);
    }
}
