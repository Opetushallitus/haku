package fi.vm.sade.oppija.lomake.domain.elements.questions;

import fi.vm.sade.oppija.lomake.domain.I18nText;

/**
 * @author Mikko Majapuro
 */
public class DateQuestion extends Question {

    private static final long serialVersionUID = -4739850221793918562L;


    public DateQuestion(final String id, final I18nText i18nText) {
        super(id, i18nText);
        addAttribute("type", "text");
        addAttribute("placeholder", "pp.kk.vvvv");
    }
}
