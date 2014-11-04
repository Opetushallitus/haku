package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DateQuestion;

public class DateQuestionBuilder extends QuestionBuilder {

    protected DateQuestionBuilder(String id) {
        super(id);
    }

    @Override
    Element buildImpl() {
        return new DateQuestion(id, i18nText);
    }

    public static DateQuestionBuilder Date(final String id) {
        return new DateQuestionBuilder(id);
    }
}
