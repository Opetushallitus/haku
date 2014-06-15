package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

public class TextQuestionBuilder extends ElementBuilder {

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
