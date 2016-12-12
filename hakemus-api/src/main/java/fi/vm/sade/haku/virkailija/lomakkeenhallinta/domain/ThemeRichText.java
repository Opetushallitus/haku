package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.RichText;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Regexp;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

public class ThemeRichText extends ThemeQuestion {

    @JsonCreator
    public ThemeRichText(@JsonProperty(value = "applicationSystemId") String applicationSystemId,
                         @JsonProperty(value = "theme") String theme,
                         @JsonProperty(value = "learningOpportunityId") String learningOpportunityId,
                         @JsonProperty(value = "targetIsGroup") Boolean targetIsGroup,
                         @JsonProperty(value = "ordinal") Integer ordinal,
                         @JsonProperty(value = "validators") Map<String, String> validators,
                         @JsonProperty(value = "attachmentRequests") List<AttachmentRequest> attachmentRequests){
        super(applicationSystemId, theme, learningOpportunityId, targetIsGroup, ordinal, validators, attachmentRequests);
    }

    public ThemeRichText() {
        super();
    }

    public ThemeRichText(String applicationSystemId, String theme, String creatorPersonOid, List<String> ownerOrganizationOid, String learningOpportunityId, Boolean targetIsGroup, Integer ordinal, Map<String, String> validators, List<AttachmentRequest> attachmentRequests) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, learningOpportunityId, targetIsGroup, ordinal, validators, attachmentRequests);
    }

    @Override
    public Element generateElement(final FormParameters formParameters) {
        return new RichText(this.getId().toString(), getMessageText());
    }

    @Override
    protected Expr generateAttachmentCondition(FormParameters formParameters, AttachmentRequest attachmentRequest) {
        Regexp re = new Regexp(this.getId().toString(), ".+");
        re.stripNewLines = true;
        return re;
    }

}
