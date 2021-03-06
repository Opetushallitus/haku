package fi.vm.sade.haku.oppija.lomake.domain.builder;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class OptionQuestionBuilder extends QuestionBuilder {

    protected OptionQuestionBuilder(String id) {
        super(id);
    }

    protected String defaultOption;
    protected String[] keepFirst;
    protected final List<Option> options = new ArrayList<Option>();

    public OptionQuestionBuilder emptyOption() {
        options.add(OptionBuilder.EmptyOption());
        return this;
    }

    public OptionQuestionBuilder emptyOptionDefault() {
        this.defaultOption = String.valueOf(OptionBuilder.EmptyOption());
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

    public OptionQuestionBuilder defaultOption(String value) {
        this.defaultOption = value;
        return this;
    }

    public OptionQuestionBuilder keepFirst(String... values) {
        this.keepFirst = values;
        return this;
    }
}
