package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

public class CheckBoxBuilder extends ElementBuilder {

    protected CheckBoxBuilder(String id) {
        super(id);
    }

    @Override
    public Element buildImpl() {
        return new CheckBox(id, i18nText);
    }

    public static CheckBoxBuilder Checkbox(final String id) {
        return new CheckBoxBuilder(id);
    }
}
