package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/122:15 PM}
 * @since 1.1
 */
public class CheckBox extends OptionQuestion {

    public CheckBox(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") String title, @JsonProperty(value = "name") String name) {
        super(id, title, name);
    }

}
