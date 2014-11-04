package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;

public class TextQuestionBuilder extends QuestionBuilder {

    public TextQuestionBuilder(String id) {
        super(id);
    }

    Element buildImpl() {
        return new TextQuestion(id, this.i18nText);
    }

    public static TextQuestionBuilder TextQuestion(final String id) {
        return new TextQuestionBuilder(id);
    }
}
