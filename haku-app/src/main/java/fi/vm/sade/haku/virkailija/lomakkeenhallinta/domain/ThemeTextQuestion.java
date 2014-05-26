package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;

import java.util.Map;

public class ThemeTextQuestion extends ThemeQuestion {

    public static final String TYPE = "TextQuestion";

    private Integer size;

    public ThemeTextQuestion(){
        super(TYPE);
    }

    public ThemeTextQuestion(String applicationSystemId, String theme, String creatorPersonOid, String ownerOrganizationOid, String learningOpportunityId, Map<String, String> validators) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, TYPE,learningOpportunityId, validators);
    }

    @Override
    public Element generateElement() {
        return null;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

}
