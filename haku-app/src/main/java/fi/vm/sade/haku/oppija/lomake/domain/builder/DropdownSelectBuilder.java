package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
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

    public DropdownSelectBuilder setI18nText(I18nText i18nText) {
        this.i18nText = i18nText;
        return this;
    }

    public DropdownSelectBuilder emptyOption() {
        options.add(new Option(ElementUtil.createI18NAsIs(""), ""));
        return this;
    }

    public DropdownSelectBuilder addOption(I18nText i18nText, String value) {
        options.add(new Option(i18nText, value));
        return this;
    }

    public DropdownSelectBuilder addOptions(List<Option> options) {
        this.options.addAll(options);
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
    public Element buildImpl(FormParameters formParameters) {
        DropdownSelect dropdownSelect = (DropdownSelect) this.buildImpl();
        ElementUtil.setVerboseHelp(dropdownSelect, key + ".verboseHelp", formParameters);
        return dropdownSelect;
    }

    @Override
    public Element buildImpl() {
        DropdownSelect dropdownSelect = new DropdownSelect(id, i18nText, defaultValueAttribute);
        dropdownSelect.addOptions(this.options);
        if (defaultOption != null) {
            for (Option opt : options) {
                opt.setDefaultOption(opt.getValue().equalsIgnoreCase(defaultOption));
            }
        }
        dropdownSelect.setInline(inline);
        return dropdownSelect;
    }

    public static DropdownSelectBuilder DropdownSelect(final String id) {
        return new DropdownSelectBuilder(id);
    }
}
