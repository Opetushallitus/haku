package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ThemeOptionQuestion extends ThemeQuestion {

   // Validators for the question
    private List<ThemeQuestionOption> options;

    public ThemeOptionQuestion(String type) {
        super(type);
        this.options = new ArrayList<ThemeQuestionOption>();
    }

    protected ThemeOptionQuestion(String applicationSystemId, String theme, String creatorPersonOid, List<String> ownerOrganizationOid,
      String type, String learningOpportunityId, Map<String, String> validators) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, type, learningOpportunityId, validators);
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
