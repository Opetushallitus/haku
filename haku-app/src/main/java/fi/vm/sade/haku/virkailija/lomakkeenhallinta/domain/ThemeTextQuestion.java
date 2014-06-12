package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;

public class ThemeTextQuestion extends ThemeQuestion {

    public static final String TYPE = "TextQuestion";

    private Integer size;

    public ThemeTextQuestion() {
        super(TYPE);
    }

    public ThemeTextQuestion(String applicationSystemId, String theme, String creatorPersonOid, List<String> ownerOrganizationOid, String learningOpportunityId, Map<String, String> validators) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, TYPE, learningOpportunityId, validators);
    }

    @Override
    public Element generateElement(final FormParameters formParameters) {
        TextQuestionBuilder textQuestionBuilder = TextQuestion(this.getId().toString());
        textQuestionBuilder.formParams(formParameters);
        textQuestionBuilder.i18nText(getMessageText());
        textQuestionBuilder.help(getHelpText());
        textQuestionBuilder.verboseHelp(getVerboseHelpText());
        if (this.size != null) {
            textQuestionBuilder.size(this.getSize());
        }
        textQuestionBuilder.inline();
        if (this.getRequiredFieldValidator()) {
            textQuestionBuilder.required();
        }
        return textQuestionBuilder.build();
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

}
