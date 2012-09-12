package fi.vm.sade.oppija.haku.domain.elements;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/1210:36 AM}
 * @since 1.1
 */
public class QuestionGroup extends Titled {

    public QuestionGroup(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }
}
