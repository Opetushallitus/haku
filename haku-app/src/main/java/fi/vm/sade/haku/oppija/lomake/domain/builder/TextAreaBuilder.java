package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextArea;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

public class TextAreaBuilder extends ElementBuilder {

    public static final Integer ROWS = 3;
    public static final Integer COLS = 20;

    private Integer cols = COLS;

    protected TextAreaBuilder(String id) {
        super(id);
    }

    public TextAreaBuilder cols(final int cols) {
        this.cols = cols;
        return this;
    }

    @Override
    public Element buildImpl(FormParameters formParameters) {
        TextArea textArea = (TextArea) buildImpl();
        ElementUtil.setVerboseHelp(textArea, key + ".verboseHelp", formParameters);
        return textArea;
    }

    public Element buildImpl() {
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
