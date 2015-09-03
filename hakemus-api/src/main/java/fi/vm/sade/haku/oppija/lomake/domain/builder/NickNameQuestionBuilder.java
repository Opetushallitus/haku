package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.NickNameQuestion;

public class NickNameQuestionBuilder extends TextQuestionBuilder {

    private String firstName;

    public NickNameQuestionBuilder(String id) {
        super(id);
    }

    public NickNameQuestionBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    Element buildImpl() {
        return new NickNameQuestion(id, this.i18nText, firstName);
    }

    public static NickNameQuestionBuilder NickNameQuestion(final String id) {
        return new NickNameQuestionBuilder(id);
    }
}
