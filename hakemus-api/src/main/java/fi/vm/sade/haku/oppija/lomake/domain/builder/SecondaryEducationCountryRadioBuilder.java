package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SecondaryEducationCountryRadio;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.haku.oppija.lomake.validation.validators.ValueSetValidator;

import java.util.ArrayList;
import java.util.List;

public class SecondaryEducationCountryRadioBuilder extends OptionQuestionBuilder {

    protected SecondaryEducationCountryRadioBuilder(final String id) {
        super(id);
    }

    @Override
    Element buildImpl() {
        SecondaryEducationCountryRadio radio = new SecondaryEducationCountryRadio(id, i18nText, this.options);
        List<String> values = new ArrayList<String>();
        for (Option option : options) {
            values.add(option.getValue());
            if (defaultOption != null) {
                option.setDefaultOption(option.getValue().equalsIgnoreCase(defaultOption));
            }
        }
        radio.setValidator(new ValueSetValidator("yleinen.virheellinenArvo", values));
        return radio;
    }

    public static SecondaryEducationCountryRadioBuilder SecondaryEducationCountryRadio(final String id) {
        return new SecondaryEducationCountryRadioBuilder(id);
    }
}
