package fi.vm.sade.oppija.lomake.domain.elements.questions;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Mikko Majapuro
 */
public class DateQuestion extends Question {

    private static final String datePattern = "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.(19|20)\\d\\d$";

    public DateQuestion(@JsonProperty(value = "id") String id, @JsonProperty(value = "i18nText") I18nText i18nText) {
        super(id, i18nText);
        addAttribute("type", "text");
        addAttribute("pattern", datePattern);
        addAttribute("placeholder", "pp.kk.vvvv");
    }
}
