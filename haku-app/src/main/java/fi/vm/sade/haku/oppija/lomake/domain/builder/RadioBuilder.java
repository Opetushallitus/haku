package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.OptionBuilder.Option;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;

public class RadioBuilder extends ElementBuilder {
    public static final String KYLLA = Boolean.TRUE.toString().toLowerCase();
    public static final String EI = Boolean.FALSE.toString().toLowerCase();
    private final List<Option> options = new ArrayList<Option>();
    private boolean defaultTrueFalse;
    private boolean defaultNoYes;

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
        if (defaultTrueFalse) {
            this.addOption(createI18NText("form.yleinen.kylla", formParameters.getFormMessagesBundle()), KYLLA);
            this.addOption(createI18NText("form.yleinen.ei", formParameters.getFormMessagesBundle()), EI);
        }
        if (defaultNoYes) {

            this.addOption(createI18NText("form.yleinen.ei", formParameters.getFormMessagesBundle()), EI);
            this.addOption(createI18NText("form.sora.kylla", formParameters.getFormMessagesBundle()), KYLLA);
        }
        return radio;
    }

    @Override
    public Element buildImpl() {
        Radio element = new Radio(id, i18nText);
        element.addOptions(this.options);
        element.setInline(inline);
        return element;
    }

    private RadioBuilder addOption(final I18nText i18nText, String value) {
        options.add(new Option(i18nText, value));
        return this;
    }

    public static RadioBuilder Radio(final String id) {
        return new RadioBuilder(id);
    }

    public RadioBuilder addDefaultTrueFalse() {
        this.defaultTrueFalse = true;
        return this;
    }

    public RadioBuilder noYesOption() {
        this.defaultNoYes = true;
        return this;
    }


}
