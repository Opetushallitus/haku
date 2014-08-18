package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder.Checkbox;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder.TitledGroup;

public class ThemeCheckBoxQuestion extends ThemeOptionQuestion {

    @JsonCreator
    public ThemeCheckBoxQuestion(@JsonProperty(value = "applicationSystemId") String applicationSystemId,
      @JsonProperty(value = "theme") String theme,
      @JsonProperty(value = "learningOpportunityId") String learningOpportunityId,
      @JsonProperty(value = "targetIsGroup") Boolean targetIsGroup,
      @JsonProperty(value = "ordial") Integer ordinal,
      @JsonProperty(value = "validators") Map<String,String> validators,
      @JsonProperty(value = "attachmentRequests") List<AttachmentRequest> attachmentRequests){
        super(applicationSystemId, theme, learningOpportunityId, targetIsGroup, ordinal, validators, attachmentRequests);
    }

    public ThemeCheckBoxQuestion() {
        super();
    }

    public ThemeCheckBoxQuestion(String applicationSystemId, String theme, String creatorPersonOid,
                                 List<String> ownerOrganizationOid, String learningOpportunityId, Boolean targetIsGroup, Integer ordinal,
                                 Map<String, String> validators, List<AttachmentRequest> attachmentRequests) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, learningOpportunityId, targetIsGroup, ordinal, validators, attachmentRequests);
    }

    @Override
    public Element generateElement(final FormParameters formParameters) {
        TitledGroupBuilder elementBuilder = TitledGroup(this.getId().toString());

        List<ThemeQuestionOption> options = this.getOptions();
        for (ThemeQuestionOption option : options) {
            elementBuilder.addChild(Checkbox(this.getId().toString()+"-"+option.getId())
                    .i18nText(option.getOptionText())
                    .formParams(formParameters));
        }
        elementBuilder.formParams(formParameters);
        elementBuilder.i18nText(getMessageText());
        elementBuilder.help(getHelpText());
        elementBuilder.verboseHelp(getVerboseHelpText());
        // elementBuilder.inline();

        if (this.getRequiredFieldValidator()) {
            elementBuilder.required();
        }
        for (Map.Entry<String, String> validator : this.getValidators().entrySet()) {
            String key = validator.getKey();
            if ("min".equals(key)) {
                elementBuilder.minOptions(Integer.valueOf(validator.getValue()));
            } else if ("max".equals(key)) {
                elementBuilder.maxOptions(Integer.valueOf(validator.getValue()));
            }
        }

        return elementBuilder.build();
    }
}
