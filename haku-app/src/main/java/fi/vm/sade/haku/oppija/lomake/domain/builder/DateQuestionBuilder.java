package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DateQuestion;

public class DateQuestionBuilder extends ElementBuilder {

    protected DateQuestionBuilder(String id) {
        super(id);
    }

    public DateQuestionBuilder setI18nText(I18nText i18nText) {
        this.i18nText = i18nText;
        return this;
    }

    @Override
    public Element buildImpl() {
        return new DateQuestion(id, i18nText);
    }

    public static DateQuestionBuilder Date(final String id) {
        return new DateQuestionBuilder(id);
    }
}
