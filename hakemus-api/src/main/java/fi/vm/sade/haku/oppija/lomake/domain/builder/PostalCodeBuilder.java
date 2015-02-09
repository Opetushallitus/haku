package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.PostalCode;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ValueSetValidator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

import java.util.ArrayList;
import java.util.List;

public class PostalCodeBuilder extends OptionQuestionBuilder {

    protected PostalCodeBuilder(final String id) {
        super(id);
    }

    public PostalCodeBuilder addOptions(final List<Option> options) {
        this.options.addAll(options);
        return this;
    }

    @Override
    Element buildImpl() {
        PostalCode postalCode = new PostalCode(id, i18nText, this.options);
        List<String> values = new ArrayList<String>();
        for (Option option : options) {
            values.add(option.getValue());
        }
        postalCode.setValidator(new ValueSetValidator("yleinen.virheellinenArvo", values));
        return postalCode;
    }

    public static PostalCodeBuilder PostalCode(final String id) {
        return new PostalCodeBuilder(id);
    }
}
