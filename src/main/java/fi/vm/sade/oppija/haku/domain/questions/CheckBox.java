package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/122:15 PM}
 * @since 1.1
 */
public class CheckBox extends OptionQuestion {


    private static final long serialVersionUID = -2148643791482712654L;

    public CheckBox(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") String title) {
        super(id, title);
    }

}
