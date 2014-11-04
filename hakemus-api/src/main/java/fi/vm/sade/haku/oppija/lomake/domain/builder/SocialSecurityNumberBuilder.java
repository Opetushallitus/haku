package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SocialSecurityNumber;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;

public class SocialSecurityNumberBuilder extends QuestionBuilder {
    private I18nText sexI18nText;
    private Option maleOption;
    private Option femaleOption;
    private String sexId;

    public SocialSecurityNumberBuilder(String id) {
        super(id);
    }

    public SocialSecurityNumberBuilder setSexI18nText(I18nText sexI18nText) {
        this.sexI18nText = sexI18nText;
        return this;
    }

    public SocialSecurityNumberBuilder setMaleOption(Option maleOption) {
        this.maleOption = maleOption;
        return this;
    }

    public SocialSecurityNumberBuilder setFemaleOption(Option femaleOption) {
        this.femaleOption = femaleOption;
        return this;
    }

    public SocialSecurityNumberBuilder setSexId(String sexId) {
        this.sexId = sexId;
        return this;
    }

    @Override
    Element buildImpl() {
        return new SocialSecurityNumber(id, i18nText, sexI18nText, maleOption, femaleOption, sexId);
    }
}
