package fi.vm.sade.oppija.haku.domain.questions;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.Titled;

/**
 * @author jukka
 * @version 9/7/1210:37 AM}
 * @since 1.1
 */
public abstract class Question extends Titled {

    protected Question(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title, @JsonProperty(value = "name") String name) {
        super(id, title);
    }
}
