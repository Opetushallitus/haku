package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ValueSetValidator;
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
        options.add((Option) Option(id + "." + value).setValue(value).formParams(formParameters).build());
        return this;
    }

    public RadioBuilder addOptions(final List<Option> options) {
        this.options.addAll(options);
        return this;
    }

    @Override
    Element buildImpl() {
        Radio radio = new Radio(id, i18nText, this.options);
        List<String> values = new ArrayList<String>();
        for (Option option : options) {
            values.add(option.getValue());
        }
        I18nText i18nText1 = getI18nText("yleinen.virheellinenArvo", false);
        radio.setValidator(new ValueSetValidator(i18nText1, values));
        return radio;
    }

    public static RadioBuilder Radio(final String id) {
        return new RadioBuilder(id);
    }
}
