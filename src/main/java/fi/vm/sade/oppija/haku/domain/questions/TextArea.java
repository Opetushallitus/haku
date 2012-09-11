package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author jukka
 * @version 9/7/122:03 PM}
 * @since 1.1
 */
public class TextArea extends Question {

    public TextArea(@JsonProperty(value = "id") final String id, final String title, final String name) {
        super(id, title, name);
    }
}
