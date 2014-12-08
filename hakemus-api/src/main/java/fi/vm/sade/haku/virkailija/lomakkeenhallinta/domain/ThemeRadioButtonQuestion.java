package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationOptionAttachmentRequest;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.OptionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.RelatedQuestionRuleBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Equals;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Regexp;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.RadioBuilder.Radio;

public class ThemeRadioButtonQuestion extends ThemeOptionQuestion {

    @JsonCreator
    public ThemeRadioButtonQuestion(@JsonProperty(value = "applicationSystemId") String applicationSystemId,
      @JsonProperty(value = "theme") String theme,
      @JsonProperty(value = "learningOpportunityId") String learningOpportunityId,
      @JsonProperty(value = "targetIsGroup") Boolean targetIsGroup,
      @JsonProperty(value = "ordinal") Integer ordinal,
      @JsonProperty(value = "validators") Map<String,String> validators,
      @JsonProperty(value = "attachmentRequests") List<AttachmentRequest> attachmentRequests){
        super(applicationSystemId, theme, learningOpportunityId, targetIsGroup, ordinal, validators, attachmentRequests);
    }

    public ThemeRadioButtonQuestion() {
        super();
    }

    public ThemeRadioButtonQuestion(String applicationSystemId, String theme, String creatorPersonOid, List<String> ownerOrganizationOid, String learningOpportunityId, Boolean targetIsGroup, Integer ordinal, Map<String, String> validators, List<AttachmentRequest> attachmentRequests) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, learningOpportunityId, targetIsGroup, ordinal, validators, attachmentRequests);
        this.setRequiredFieldValidator(Boolean.TRUE);
    }

    @Override
    public Element generateElement(final FormParameters formParameters) {
        RadioBuilder elementBuilder = Radio(this.getId().toString());

        List<ThemeQuestionOption> options = this.getOptions();
        List<Option> elementList = new ArrayList<Option>(options.size());
        for (ThemeQuestionOption option : options) {
            elementList.add((Option) OptionBuilder.Option(ElementUtil.randomId())
                    .setValue(option.getId())
                    .i18nText(option.getOptionText())
                    .formParams(formParameters).build());
        }
        elementBuilder.addOptions(elementList);
        elementBuilder.formParams(formParameters);
        elementBuilder.i18nText(getMessageText());
        elementBuilder.help(getHelpText());
        elementBuilder.verboseHelp(getVerboseHelpText());

        addAoidOrAoidGroup(elementBuilder);
        //elementBuilder.inline();

        // Radiobuttons are always required
        elementBuilder.required();
        return elementBuilder.build();
    }

    @Override
    protected Expr generateAttachmentCondition(FormParameters formParameters, AttachmentRequest attachmentRequest) {
        String optionId =attachmentRequest.getAttachedToOptionId();
        Expr expr;
        if (null != optionId) {
            expr = ExprUtil.equals(this.getId().toString(), optionId);
        }
        else {
            List<String> optionsIds = new ArrayList<String>();
            for (ThemeQuestionOption option: this.getOptions()){
                optionsIds.add(option.getId());
            }
            expr = ExprUtil.atLeastOneValueEqualsToVariable(this.getId().toString(), optionsIds.toArray(new String[optionsIds.size()]));
        }
        return expr;
    }
}
