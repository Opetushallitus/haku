package fi.vm.sade.haku.oppija.lomake.domain.elements.questions;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Titled;

public abstract class Question extends Titled {
    public Question(String id, I18nText i18nText) {
        super(id, i18nText);
    }
}
