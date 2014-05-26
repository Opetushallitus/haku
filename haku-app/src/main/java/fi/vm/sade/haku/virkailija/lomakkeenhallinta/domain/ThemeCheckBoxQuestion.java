package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;

import java.util.Map;

public class ThemeCheckBoxQuestion extends ThemeOptionQuestion {

    public static final String TYPE = "CheckBox";

    public ThemeCheckBoxQuestion(){
        super(TYPE);
    }

    public ThemeCheckBoxQuestion(String applicationSystemId, String theme, String creatorPersonOid, String ownerOrganizationOid, String learningOpportunityId, Map<String, String> validators) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, TYPE,learningOpportunityId, validators);
    }

    @Override
    public Element generateElement() {
        return null;
    }
}
