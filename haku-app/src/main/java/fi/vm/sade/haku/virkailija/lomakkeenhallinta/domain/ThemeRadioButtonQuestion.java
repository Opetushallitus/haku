package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemeRadioButtonQuestion extends ThemeOptionQuestion {

    public static final String TYPE = "RadioButton";

    public ThemeRadioButtonQuestion(){
        super(TYPE);
    }

    public ThemeRadioButtonQuestion(String applicationSystemId, String theme, String creatorPersonOid, List<String> ownerOrganizationOid, String learningOpportunityId, Map<String, String> validators) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, TYPE,learningOpportunityId, validators);
    }

    @Override
    public Element generateElement(final FormParameters formParameters) {
        return null;
    }
}
