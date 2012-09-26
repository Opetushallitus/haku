package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author jukka
 * @version 9/7/122:03 PM}
 * @since 1.1
 */
public class TextArea extends Question {

    private static final long serialVersionUID = 3485187810260760341L;

    public TextArea(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
        addAttribute("rows", "3");
        addAttribute("cols", "20");
    }
}
