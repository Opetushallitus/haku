package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class CustomLanguageRow extends LanguageRow {

    private List<String> scopeOptions;

    public CustomLanguageRow(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                             @JsonProperty(value = "scope") List<String> scope,
                             @JsonProperty(value = "scopeOptions") List<String> languages) {
        super(id, title, scope);
        this.scopeOptions = languages;
    }

    public List<String> getScopeOptions() {
        return scopeOptions;
    }
}
