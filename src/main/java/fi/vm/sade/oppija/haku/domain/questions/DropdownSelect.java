package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/122:16 PM}
 * @since 1.1
 */
public class DropdownSelect extends OptionQuestion {

    private static final long serialVersionUID = -6175691634412691296L;

    public DropdownSelect(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }
}
