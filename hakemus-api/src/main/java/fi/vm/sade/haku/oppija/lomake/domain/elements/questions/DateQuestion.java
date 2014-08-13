package fi.vm.sade.haku.oppija.lomake.domain.elements.questions;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;

/**
 * @author Mikko Majapuro
 */
public class DateQuestion extends Titled {

    private static final long serialVersionUID = -4739850221793918562L;

    public DateQuestion(final String id, final I18nText i18nText) {
        super(id, i18nText);
    }
}
