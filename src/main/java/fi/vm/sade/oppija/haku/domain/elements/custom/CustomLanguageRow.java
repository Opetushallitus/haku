package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.questions.Option;

import java.util.List;

/**
 * Models a grade grid row that has an input for language option and
 * scope (A1, B1 etc) option. Language options are listed in options list
 * provided by OptionQuestion and scope options are listed in scope options variable.
 *
 * @author Hannu Lyytikainen
 */
public class CustomLanguageRow extends LanguageRow {

    private List<Option> scopeOptions;

    public CustomLanguageRow(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                             @JsonProperty(value = "scopeOptions") List<Option> scopeOptions) {
        super(id, title);
        this.scopeOptions = scopeOptions;
    }

    public void addScopeOption(final String id, final String value, final String title) {
        this.scopeOptions.add(new Option(this.getId() + ID_DELIMITER + id, value, title));
    }

    public List<Option> getScopeOptions() {
        return scopeOptions;
    }

}
