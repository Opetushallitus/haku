package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class LanguageRow extends SubjectRow {

    private List<String> languageOptions;

    public LanguageRow(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                       @JsonProperty(value = "languages") List<String> languages) {
        super(id, title);
        this.languageOptions = languages;
    }

    public List<String> getLanguageOptions() {
        return languageOptions;
    }
}
