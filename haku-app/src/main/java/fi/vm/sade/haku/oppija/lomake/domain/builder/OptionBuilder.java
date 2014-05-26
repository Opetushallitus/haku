package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

public class OptionBuilder extends ElementBuilder {
    private String value;

    public OptionBuilder() {
        super(ElementUtil.randomId());
    }

    public OptionBuilder(final String id) {
        super(id);
    }

    public OptionBuilder setValue(final String value) {
        this.value = value;
        return this;
    }

    public OptionBuilder setI18nText(I18nText i18nText) {
        this.i18nText = i18nText;
        return this;
    }

    @Override
    public Element buildImpl(final FormParameters formParameters) {
        Option option = (Option) buildImpl();
        ElementUtil.setVerboseHelp(option, key + ".verboseHelp", formParameters);
        return option;
    }

    @Override
    public Element buildImpl() {
        Option element = new Option(this.i18nText, value);
        element.setInline(this.inline);
        return element;
    }

    public static OptionBuilder Option(final String id) {
        return new OptionBuilder(id);
    }

    public static Option EmptyOption() {
        return (Option) Option(ElementUtil.randomId())
                .setValue("")
                .i18nText(ElementUtil.createI18NAsIs("")).build();
    }


}
