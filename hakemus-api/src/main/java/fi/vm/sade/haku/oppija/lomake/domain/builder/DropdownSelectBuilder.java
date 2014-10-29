package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ValueSetValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.ArrayList;
import java.util.List;

public class DropdownSelectBuilder extends OptionQuestionBuilder {

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
