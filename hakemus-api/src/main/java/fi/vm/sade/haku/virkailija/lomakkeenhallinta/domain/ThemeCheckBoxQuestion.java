package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import com.google.common.collect.ImmutableSet;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ExprUtil;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.*;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder.Checkbox;
import static fi.vm.sade.haku.oppija.lomake.domain.builder.TitledGroupBuilder.TitledGroup;
import static fi.vm.sade.haku.oppija.lomake.util.StringUtil.safeToString;

public class ThemeCheckBoxQuestion extends ThemeOptionQuestion {

    @JsonCreator
    public ThemeCheckBoxQuestion(@JsonProperty(value = "applicationSystemId") String applicationSystemId,
      @JsonProperty(value = "theme") String theme,
      @JsonProperty(value = "learningOpportunityId") String learningOpportunityId,
      @JsonProperty(value = "targetIsGroup") Boolean targetIsGroup,
      @JsonProperty(value = "ordinal") Integer ordinal,
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
            CheckBoxBuilder checkbox = Checkbox(this.getId().toString() + "-" + option.getId());
            addAoidOrAoidGroup(checkbox);
            elementBuilder
                    .addChild(checkbox
                            .excelColumnLabel(buildExcelColumnLabel(option.getOptionText(), formParameters))
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
            String value = validator.getValue();
            if ("min".equals(key)) {
                if (null != value && !"".equals(value)) {
                    elementBuilder.minOptions(Integer.valueOf(value));
                }
            } else if ("max".equals(key)) {
                if (null != value && !"".equals(value)) {
                    elementBuilder.maxOptions(Integer.valueOf(value));
                }
            }
        }

        return elementBuilder.build();
    }

    private I18nText buildExcelColumnLabel(I18nText optionText, FormParameters formParameters) {
        I18nText parentText = getMessageText();
        ImmutableSet<String> allAvailableLanguages = ImmutableSet.<String>builder().addAll(optionText.getAvailableLanguages())
                .addAll(parentText.getAvailableLanguages()).build();
        Map<String, String> translations = new HashMap<>(allAvailableLanguages.size());
        for (String lang : allAvailableLanguages) {
            translations.put(lang, safeToString(parentText.getTextOrNull(lang))
                            + ":"
                            + safeToString(optionText.getTextOrNull(lang)));
        }
        return new I18nText(translations);
    }

    @Override
    protected Expr generateAttachmentCondition(FormParameters formParameters, AttachmentRequest attachmentRequest) {
        String optionId = attachmentRequest.getAttachedToOptionId();
        Expr expr;
        if (null != optionId) {
            expr =  ExprUtil.equals(this.getId().toString()+"-"+ optionId, "true");
        }
        else {
            List<String> answerIds = new ArrayList<String>();
            for (ThemeQuestionOption option: this.getOptions()){
                answerIds.add(this.getId().toString() +"-"+ option.getId());
            }
            expr = ExprUtil.atLeastOneVariableContainsValue("true", answerIds.toArray(new String[answerIds.size()]));
        }
        return expr;
    }
}
