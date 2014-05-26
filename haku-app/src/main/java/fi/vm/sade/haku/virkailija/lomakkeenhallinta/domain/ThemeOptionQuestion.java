package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;

import java.util.List;
import java.util.Map;

public abstract class ThemeOptionQuestion extends ThemeQuestion {

    public static final String TYPE = "TextQuestion";

    // Validators for the question
    private List<ThemeQuestionOption> options;

    public ThemeOptionQuestion(String type) {
        super(type);
    }

    protected ThemeOptionQuestion(String applicationSystemId, String theme, String creatorPersonOid, String ownerOrganizationOid,
      String type, String learningOpportunityId, Map<String, String> validators) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, type, learningOpportunityId, validators);
    }

    public List<ThemeQuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<ThemeQuestionOption> options) {
        this.options = options;
    }

    @Override
    public Element generateElement() {
        return null;
    }
}
