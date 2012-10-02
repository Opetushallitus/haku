package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.questions.OptionQuestion;

import java.util.List;

/**
 * Renders as a language grade row in grade grid. Title is used to hold the scope
 * of the language studies (A1, B2 etc) and options (defined in OptionQuestion) are used to hold
 * the different languages.
 *
 * @author Hannu Lyytikainen
 */
public class LanguageRow extends SubjectRow {

    public LanguageRow(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title) {
        super(id, title);
    }

}
