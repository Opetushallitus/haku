package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextArea;

public class TextAreaBuilder extends QuestionBuilder {

    public static final Integer ROWS = 4;
    public static final Integer COLS = 60;

    private Integer cols = COLS;

    protected TextAreaBuilder(String id) {
        super(id);
    }

    public TextAreaBuilder cols(final int cols) {
        this.cols = cols;
        return this;
    }

    Element buildImpl() {
        TextArea textArea = new TextArea(id, i18nText);
        textArea.addAttribute("rows", ROWS.toString());
        textArea.setInline(this.inline);
        if (cols != null) {
            textArea.addAttribute("cols", cols.toString());
        }
        return textArea;

    }

    public static final TextAreaBuilder TextArea(final String id) {
        return new TextAreaBuilder(id);
    }
}
