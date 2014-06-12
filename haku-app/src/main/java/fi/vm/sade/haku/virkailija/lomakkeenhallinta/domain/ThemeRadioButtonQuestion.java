package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.builder.OptionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder.Radio;

public class ThemeRadioButtonQuestion extends ThemeOptionQuestion {

    public static final String TYPE = "RadioButton";

    public ThemeRadioButtonQuestion() {
        super(TYPE);
    }

    public ThemeRadioButtonQuestion(String applicationSystemId, String theme, String creatorPersonOid, List<String> ownerOrganizationOid, String learningOpportunityId, Map<String, String> validators) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, TYPE, learningOpportunityId, validators);
    }

    @Override
    public Element generateElement(final FormParameters formParameters) {
        RadioBuilder elementBuilder = Radio(this.getId().toString());

        List<ThemeQuestionOption> options = this.getOptions();
        List<Option> elementList = new ArrayList<Option>(options.size());
        for (ThemeQuestionOption option : options) {
            elementList.add((Option) new OptionBuilder(option.getId())
                    .i18nText(option.getOptionText())
                    .formParams(formParameters).build());
        }
        elementBuilder.addOptions(elementList);
        elementBuilder.formParams(formParameters);
        elementBuilder.i18nText(getMessageText());
        elementBuilder.help(getHelpText());
        elementBuilder.verboseHelp(getVerboseHelpText());
        elementBuilder.inline();

        if (this.getRequiredFieldValidator()) {
            elementBuilder.required();
        }
        return elementBuilder.build();
    }
}
