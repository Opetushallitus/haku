package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

import java.util.ArrayList;
import java.util.List;

public abstract class OptionQuestionBuilder extends QuestionBuilder {

    protected OptionQuestionBuilder(String id) {
        super(id);
    }

    protected String defaultValueAttribute;
    protected String defaultOption;
    protected String[] keepFirst;
    protected final List<Option> options = new ArrayList<Option>();

    public OptionQuestionBuilder emptyOption() {
        options.add(OptionBuilder.EmptyOption());
        return this;
    }

    public OptionQuestionBuilder addOption(String value, FormParameters formParameters) {
        options.add((Option) new OptionBuilder(id + "." + value)
                .setValue(value)
                .formParams(formParameters)
                .build());
        return this;
    }

    public OptionQuestionBuilder addOption(I18nText i18nText, String value) {
        options.add((Option) new OptionBuilder()
                .setValue(value)
                .i18nText(i18nText)
                .build());
        return this;
    }

    public OptionQuestionBuilder addOptions(List<Option> options) {
        this.options.addAll(options);
        return this;
    }

    public OptionQuestionBuilder addOption(Option option) {
        this.options.add(option);
        return this;
    }

    public OptionQuestionBuilder defaultValueAttribute(String attributeName) {
        this.defaultValueAttribute = attributeName;
        return this;
    }

    public OptionQuestionBuilder defaultOption(String fin) {
        this.defaultOption = fin;
        return this;
    }

    public OptionQuestionBuilder keepFirst(String... values) {
        this.keepFirst = values;
        return this;
    }

}
