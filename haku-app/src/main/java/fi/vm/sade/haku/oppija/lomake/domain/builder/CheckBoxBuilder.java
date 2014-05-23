package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.CheckBox;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

public class CheckBoxBuilder extends ElementBuilder {

    protected CheckBoxBuilder(String id) {
        super(id);
    }

    @Override
    public Element buildImpl(FormParameters formParameters) {
        CheckBox checkBox = (CheckBox) this.buildImpl();
        ElementUtil.setVerboseHelp(checkBox, key + ".verboseHelp", formParameters);
        return checkBox;
    }

    @Override
    public Element buildImpl() {
        CheckBox checkBox = new CheckBox(id, i18nText);
        checkBox.setInline(this.inline);
        return checkBox;
    }

    public static CheckBoxBuilder Checkbox(final String id) {
        return new CheckBoxBuilder(id);
    }
}
