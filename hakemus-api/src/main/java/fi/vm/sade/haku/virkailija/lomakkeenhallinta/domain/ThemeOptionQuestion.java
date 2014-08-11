package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ThemeOptionQuestion extends ThemeQuestion {

   // Validators for the question
    private List<ThemeQuestionOption> options;

    protected ThemeOptionQuestion(String applicationSystemId, String theme, String learningOpportunityId,
      Boolean targetIsGroup, Integer ordinal, Map<String,String> validators, List<AttachmentRequest> attachmentRequests){
        super(applicationSystemId, theme,learningOpportunityId, targetIsGroup, ordinal, validators, attachmentRequests);
    }

    protected ThemeOptionQuestion() {
        super();
        this.options = new ArrayList<ThemeQuestionOption>();
    }

    protected ThemeOptionQuestion(String applicationSystemId, String theme, String creatorPersonOid, List<String> ownerOrganizationOid,
      String learningOpportunityId, Boolean targetIsGroup, Integer ordinal, Map<String, String> validators, List<AttachmentRequest> attachmentRequests) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, learningOpportunityId, targetIsGroup, ordinal, validators, attachmentRequests);
        this.options = new ArrayList<ThemeQuestionOption>();
    }

    public List<ThemeQuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<ThemeQuestionOption> options) {
        this.options = new ArrayList<ThemeQuestionOption>(options);
    }

    @Override
    public Element generateElement(final FormParameters formParameters) {
        return null;
    }
}
