package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

public class TextQuestionBuilder extends ElementBuilder {

    public TextQuestionBuilder(String id) {
        super(id);
    }

    public TextQuestion buildImpl(final FormParameters formParameters) {
        TextQuestion textQuestion = (TextQuestion) buildImpl();
        ElementUtil.setVerboseHelp(textQuestion, key + ".verboseHelp", formParameters);
        return textQuestion;
    }

    public Element buildImpl() {
        TextQuestion textQuestion = new TextQuestion(id, this.i18nText);
        textQuestion.setInline(this.inline);
        return textQuestion;
    }

    public static TextQuestionBuilder TextQuestion(final String id) {
        return new TextQuestionBuilder(id);
    }
}
