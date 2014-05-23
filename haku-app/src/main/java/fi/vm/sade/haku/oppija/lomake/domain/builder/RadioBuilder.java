package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.OptionBuilder.Option;

public class RadioBuilder extends ElementBuilder {
    private final List<Option> options = new ArrayList<Option>();

    protected RadioBuilder(final String id) {
        super(id);
    }

    public RadioBuilder addOption(String value, FormParameters formParameters) {
        options.add((Option) Option(id + "." + value).setValue(value).build(formParameters));
        return this;
    }

    public RadioBuilder addOptions(final List<Option> options) {
        this.options.addAll(options);
        return this;
    }

    @Override
    public Element buildImpl(FormParameters formParameters) {
        Radio radio = (Radio) this.buildImpl();
        ElementUtil.setVerboseHelp(radio, key + ".verboseHelp", formParameters);
        return radio;
    }

    @Override
    public Element buildImpl() {
        Radio element = new Radio(id, i18nText, this.options);
        element.setInline(inline);
        return element;
    }

    public static RadioBuilder Radio(final String id) {
        return new RadioBuilder(id);
    }
}
