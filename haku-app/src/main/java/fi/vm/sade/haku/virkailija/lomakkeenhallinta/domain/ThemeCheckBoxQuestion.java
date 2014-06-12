package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder.Checkbox;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder.TitledGroup;

public class ThemeCheckBoxQuestion extends ThemeOptionQuestion {

    public static final String TYPE = "CheckBox";

    public ThemeCheckBoxQuestion() {
        super(TYPE);
    }

    public ThemeCheckBoxQuestion(String applicationSystemId, String theme, String creatorPersonOid, List<String> ownerOrganizationOid, String learningOpportunityId, Map<String, String> validators) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, TYPE, learningOpportunityId, validators);
    }

    @Override
    public Element generateElement(final FormParameters formParameters) {
        TitledGroupBuilder elementBuilder = TitledGroup(this.getId().toString());

        List<ThemeQuestionOption> options = this.getOptions();
        for (ThemeQuestionOption option : options) {
            elementBuilder.addChild(Checkbox(option.getId().toString())
                    .i18nText(option.getOptionText())
                    .inline()
                    .formParams(formParameters));
        }
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
