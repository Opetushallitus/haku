package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;

import java.util.Map;
import java.util.Set;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.addMaxLengthAttributeAndLengthValidator;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.addSizeAttribute;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NText;

public class ThemeTextQuestion extends ThemeQuestion {

    public static enum Fields  {
        MESSAGE, HELP, VERBOSE_HELP
    }
    // ERRORS ARE VALIDATOR BASED AKA CODED

    public ThemeTextQuestion(){
    }

    public ThemeTextQuestion(String applicationSystemId, Theme theme, String creatorPersonOid, String ownerOrganizationOid, Map<String, String> parameters, String learningOpportunityProviderId) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, Type.TEXT_QUESTION, parameters, learningOpportunityProviderId);
    }

    //TODO: Fix with implementation
    @Override
    public Element generateElement() {
        return null; 
    }
}
