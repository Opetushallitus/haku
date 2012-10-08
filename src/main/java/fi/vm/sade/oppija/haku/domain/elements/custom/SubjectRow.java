package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.questions.Question;

/**
 * @author Hannu Lyytikainen
 */
public class SubjectRow extends Question {
    public SubjectRow(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }
}
