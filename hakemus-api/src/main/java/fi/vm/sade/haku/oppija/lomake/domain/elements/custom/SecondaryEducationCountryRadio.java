package fi.vm.sade.haku.oppija.lomake.domain.elements.custom;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.OptionQuestion;

import java.util.List;

public class SecondaryEducationCountryRadio extends OptionQuestion {
    public SecondaryEducationCountryRadio (final String id, final I18nText i18nText, final List<Option> options) {
        super(id, i18nText, options, null, false);
    }
}
