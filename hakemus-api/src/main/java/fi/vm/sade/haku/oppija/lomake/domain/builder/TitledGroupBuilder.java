package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.TitledGroup;

public class TitledGroupBuilder extends TitledBuilder {

    protected TitledGroupBuilder(String id) {
        super(id);
    }
    @Override
    Element buildImpl() {
        return new TitledGroup(id, i18nText);
    }

    public static TitledGroupBuilder TitledGroup(final String id) {
        return new TitledGroupBuilder(id);
    }
}
