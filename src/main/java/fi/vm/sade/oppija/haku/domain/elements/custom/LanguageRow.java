package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class LanguageRow extends SubjectRow {

    private List<String> scope;

    public LanguageRow(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                       @JsonProperty(value = "scope") List<String> scope) {
        super(id, title);
        this.scope = scope;
    }

    public List<String> getScope() {
        return scope;
    }
}
