package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DateQuestion;

public class DateQuestionBuilder extends QuestionBuilder {

    private boolean allowFutureDates;

    protected DateQuestionBuilder(String id) {
        super(id);
        this.allowFutureDates = false;
    }

    @Override
    Element buildImpl() {
        return new DateQuestion(id, i18nText, allowFutureDates);
    }

    public static DateQuestionBuilder Date(final String id) {
        return new DateQuestionBuilder(id);
    }

    public DateQuestionBuilder allowFutureDates() {
        this.allowFutureDates = true;
        return this;
    }
}
