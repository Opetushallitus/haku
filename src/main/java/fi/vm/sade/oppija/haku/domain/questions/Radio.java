package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/122:04 PM}
 * @since 1.1
 */
public class Radio extends OptionQuestion {

    private static final long serialVersionUID = 5782376533955721032L;

    public Radio(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }
}
