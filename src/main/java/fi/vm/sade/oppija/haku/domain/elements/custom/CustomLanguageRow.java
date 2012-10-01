package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class CustomLanguageRow extends LanguageRow {

    private List<String> languages;

    public CustomLanguageRow(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                             @JsonProperty(value = "scope") List<String> scope,
                             @JsonProperty(value = "languages") List<String> languages) {
        super(id, title, scope);
        this.languages = languages;
    }

    public List<String> getLanguages() {
        return languages;
    }
}
