package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.TitledGroup;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

public class TitledGroupBuilder extends ElementBuilder {

    protected TitledGroupBuilder(String id) {
        super(id);
    }

    @Override
    public Element buildImpl(FormParameters formParameters) {
        TitledGroup titledGroup = (TitledGroup) this.buildImpl();
        ElementUtil.setVerboseHelp(titledGroup, key + ".verboseHelp", formParameters);
        return titledGroup;
    }

    @Override
    public Element buildImpl() {
        TitledGroup titledGroup = new TitledGroup(id, i18nText);
        titledGroup.setInline(this.inline);
        return titledGroup;
    }

    public static TitledGroupBuilder TitledGroup(final String id) {
        return new TitledGroupBuilder(id);
    }
}
