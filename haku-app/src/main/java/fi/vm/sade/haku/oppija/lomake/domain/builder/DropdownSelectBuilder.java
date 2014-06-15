package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ValueSetValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.ArrayList;
import java.util.List;

public class DropdownSelectBuilder extends ElementBuilder {
    private String defaultValueAttribute;
    private String defaultOption;
    private final List<Option> options = new ArrayList<Option>();

    public DropdownSelectBuilder(String id) {
        super(id);
    }

    public DropdownSelectBuilder emptyOption() {
        options.add(OptionBuilder.EmptyOption());
        return this;
    }

    public DropdownSelectBuilder addOption(I18nText i18nText, String value) {
        options.add((Option) new OptionBuilder()
                .setValue(value)
                .i18nText(i18nText)
                .build());
        return this;
    }

    public DropdownSelectBuilder addOptions(List<Option> options) {
        this.options.addAll(options);
        return this;
    }

    public DropdownSelectBuilder addOption(Option option) {
        this.options.add(option);
        return this;
    }

    public DropdownSelectBuilder defaultValueAttribute(String attributeName) {
        this.defaultValueAttribute = attributeName;
        return this;
    }

    public DropdownSelectBuilder defaultOption(String fin) {
        this.defaultOption = fin;
        return this;
    }

    @Override
    Element buildImpl() {
        if (defaultOption != null) {
            for (Option opt : options) {
                opt.setDefaultOption(opt.getValue().equalsIgnoreCase(defaultOption));
            }
        }
        DropdownSelect dropdownSelect = new DropdownSelect(id, this.i18nText, this.options, defaultValueAttribute);
        List<String> values = new ArrayList<String>();
        for (Option option : options) {
            values.add(option.getValue());
        }
        dropdownSelect.setValidator(new ValueSetValidator(ElementUtil.createI18NText("yleinen.virheellinenArvo"), values));
        return dropdownSelect;
    }

    public static DropdownSelectBuilder Dropdown(final String id) {
        return new DropdownSelectBuilder(id);
    }
}
