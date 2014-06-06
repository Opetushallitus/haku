package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Text;

public class TextBuilder extends ElementBuilder {

    protected TextBuilder(String id) {
        super(id);
    }

    public TextBuilder setI18nText(I18nText i18nText) {
        this.i18nText = i18nText;
        return this;
    }

    public Element buildImpl() {
        return new Text(id, i18nText);
    }

    public static TextBuilder Text(final String id) {
        return new TextBuilder(id);
    }
}
