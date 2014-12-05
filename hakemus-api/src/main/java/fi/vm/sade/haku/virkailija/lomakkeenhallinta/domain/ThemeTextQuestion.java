package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Regexp;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder.TextQuestion;

public class ThemeTextQuestion extends ThemeQuestion {

    private Integer size;

    @JsonCreator
    public ThemeTextQuestion(@JsonProperty(value = "applicationSystemId") String applicationSystemId,
      @JsonProperty(value = "theme") String theme,
      @JsonProperty(value = "learningOpportunityId") String learningOpportunityId,
      @JsonProperty(value = "targetIsGroup") Boolean targetIsGroup,
      @JsonProperty(value = "ordinal") Integer ordinal,
      @JsonProperty(value = "validators") Map<String,String> validators,
      @JsonProperty(value = "attachmentRequests") List<AttachmentRequest> attachmentRequests){
        super(applicationSystemId, theme, learningOpportunityId, targetIsGroup, ordinal, validators, attachmentRequests);
    }

    public ThemeTextQuestion() {
        super();
    }

    public ThemeTextQuestion(String applicationSystemId, String theme, String creatorPersonOid, List<String> ownerOrganizationOid, String learningOpportunityId, Boolean targetIsGroup, Integer ordinal, Map<String, String> validators, List<AttachmentRequest> attachmentRequests) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, learningOpportunityId, targetIsGroup, ordinal, validators, attachmentRequests);
    }

    @Override
    public Element generateElement(final FormParameters formParameters) {
        TextQuestionBuilder elementBuilder = TextQuestion(this.getId().toString());
        elementBuilder.formParams(formParameters);
        elementBuilder.i18nText(getMessageText());
        elementBuilder.help(getHelpText());
        elementBuilder.verboseHelp(getVerboseHelpText());
        elementBuilder.size(50);
        if (this.size != null) {
            elementBuilder.maxLength(this.getSize());
        }
        //elementBuilder.inline();
        if (this.getRequiredFieldValidator()) {
            elementBuilder.required();
        }
        addAoidOrAoidGroup(elementBuilder);
        return elementBuilder.build();
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    protected Expr generateAttachmentCondition(FormParameters formParameters, AttachmentRequest attachmentRequest) {
        Regexp expr = new Regexp(this.getId().toString(), ".+");
        return expr;
    }
}
