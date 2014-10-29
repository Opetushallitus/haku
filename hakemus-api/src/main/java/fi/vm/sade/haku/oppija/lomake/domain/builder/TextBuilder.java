package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Text;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

public class TextBuilder extends TitledBuilder {

    protected TextBuilder(String id) {
        super(id);
    }

    Element buildImpl() {
        return new Text(id, i18nText);
    }

    public static TextBuilder Text(final String id) {
        return new TextBuilder(id);
    }
    public static TextBuilder Text() {
        return new TextBuilder(ElementUtil.randomId());
    }
}

