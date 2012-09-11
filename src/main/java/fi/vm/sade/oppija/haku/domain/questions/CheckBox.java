package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/122:15 PM}
 * @since 1.1
 */
public class CheckBox extends OptionQuestion {

    public CheckBox(@JsonProperty(value = "id") final String id, String title, String name) {
        super(id, title, name);
    }

}
