package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;


import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;

import java.util.Map;

public class ThemeTextQuestion extends ThemeQuestion {

    public static final String TYPE = "TextQuestion";

    private Boolean requiredFieldValidator = Boolean.FALSE;

    private Integer size;

    private Boolean previewable = Boolean.FALSE;

    public ThemeTextQuestion(){
        super(TYPE);
    }

    public ThemeTextQuestion(String applicationSystemId, String theme, String creatorPersonOid, String ownerOrganizationOid, String learningOpportunityProviderId, Map<String, String> validators) {
        super(applicationSystemId, theme, creatorPersonOid, ownerOrganizationOid, TYPE,learningOpportunityProviderId, validators);
    }

    @Override
    public Element generateElement() {
        return null;
    }

    public Boolean getRequiredFieldValidator() {
        return requiredFieldValidator;
    }

    public void setRequiredFieldValidator(Boolean requiredFieldValidator) {
        this.requiredFieldValidator = requiredFieldValidator;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Boolean getPreviewable() {
        return previewable;
    }

    public void setPreviewable(Boolean previewable) {
        this.previewable = previewable;
    }
}
