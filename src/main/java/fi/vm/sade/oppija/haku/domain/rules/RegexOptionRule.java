package fi.vm.sade.oppija.haku.domain.rules;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.questions.Option;

/**
 * @author jukka
 * @version 10/4/123:25 PM}
 * @since 1.1
 */
public class RegexOptionRule {
    private final String regex;
    private final Option option;

    public RegexOptionRule(@JsonProperty(value = "regex") String regex, @JsonProperty(value = "option") Option option) {
        this.regex = regex;
        this.option = option;
    }

    public String getRegex() {
        return regex;
    }

    public Option getOption() {
        return option;
    }
}
