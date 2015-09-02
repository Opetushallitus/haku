package fi.vm.sade.haku.oppija.lomake.domain.elements.questions;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;

/**
 * @author Mikko Majapuro
 */
public class DateQuestion extends Question {

    private static final long serialVersionUID = -5682360214510051875L;
    private boolean allowFutureDates;

    public DateQuestion(final String id, final I18nText i18nText, final boolean allowFutureDates) {
        super(id, i18nText);
        this.allowFutureDates = allowFutureDates;
    }

    public boolean isAllowFutureDates() {
        return allowFutureDates;
    }

    public void setAllowFutureDates(boolean allowFutureDates) {
        this.allowFutureDates = allowFutureDates;
    }
}
