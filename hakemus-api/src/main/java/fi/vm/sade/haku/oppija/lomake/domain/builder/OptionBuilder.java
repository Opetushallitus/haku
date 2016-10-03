package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

public class OptionBuilder extends QuestionBuilder {
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

    @Override
    Element buildImpl() {
        return new Option(this.i18nText, value);
    }

    public static OptionBuilder Option(final String id) {
        return new OptionBuilder(id);
    }


    public static Option EmptyOption() {
        return (Option) Option(ElementUtil.randomId())
                .setValue(Option.EMPTY_VALUE_PLACEHOLDER)
                .i18nText(ElementUtil.createI18NAsIs(Option.EMPTY_VALUE_PLACEHOLDER)).build();
    }
}
