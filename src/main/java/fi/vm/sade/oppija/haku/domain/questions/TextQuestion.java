package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/121:28 PM}
 * @since 1.1
 */
public class TextQuestion extends Question {

    public TextQuestion(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title, @JsonProperty(value = "name") final String name) {
        super(id, title, name);
    }
}
