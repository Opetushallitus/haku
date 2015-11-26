package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ValueSetValidator;

import java.util.ArrayList;
import java.util.List;

public class DropdownSelectBuilder extends OptionQuestionBuilder {

    protected String defaultValueAttribute;
    protected Boolean useGivenOrder;

    public DropdownSelectBuilder(String id) {
        super(id);
    }

    @Override
    Element buildImpl() {
        if (defaultOption != null) {
            for (Option opt : options) {
                opt.setDefaultOption(opt.getValue().equalsIgnoreCase(defaultOption));
            }
        }
        DropdownSelect dropdownSelect = new DropdownSelect(id, this.i18nText, this.options, defaultValueAttribute, keepFirst, useGivenOrder);
        List<String> values = new ArrayList<String>();
        for (Option option : options) {
            values.add(option.getValue());
        }
        dropdownSelect.setValidator(new ValueSetValidator("yleinen.virheellinenArvo", values));
        return dropdownSelect;
    }

    public OptionQuestionBuilder emptyOptionDefault() {
        options.add(OptionBuilder.EmptyOption());
        this.defaultOption = "";
        this.defaultValueAttribute = "";
        return this;
    }

    public OptionQuestionBuilder defaultOption(String value) {
        this.defaultOption = value;
        this.defaultValueAttribute = value;
        return this;
    }

    public OptionQuestionBuilder useGivenOrder() {
        this.useGivenOrder = true;
        return this;
    }


    public OptionQuestionBuilder defaultValueAttribute(String attributeName) {
        this.defaultValueAttribute = attributeName;
        return this;
    }

    public static DropdownSelectBuilder Dropdown(final String id) {
        return new DropdownSelectBuilder(id);
    }
}
