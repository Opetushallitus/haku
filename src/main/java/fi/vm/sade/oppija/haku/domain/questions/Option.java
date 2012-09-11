package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.Titled;

/**
 * @author jukka
 * @version 9/7/1210:37 AM}
 * @since 1.1
 */
public class Option extends Titled {

    private String value;
    private String title;

    public Option(@JsonProperty(value = "id") String id, @JsonProperty(value = "value") String value, @JsonProperty(value = "title") String title) {
        super(id, title);
        this.value = value;
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }
}
