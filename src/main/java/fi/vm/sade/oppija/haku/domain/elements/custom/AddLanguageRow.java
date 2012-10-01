package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Hannu Lyytikainen
 */
public class AddLanguageRow extends SubjectRow {
    public AddLanguageRow(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id, title);
    }
}
