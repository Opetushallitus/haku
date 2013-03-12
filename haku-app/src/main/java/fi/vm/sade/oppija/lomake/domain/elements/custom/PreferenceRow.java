/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.oppija.lomake.domain.elements.custom;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.validation.validators.RequiredFieldFieldValidator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Renders as a user's application preference row. Title is used to hold the name of the preference row (Hakutoive 1, Hakutoive 2 etc.)
 * Options are used to hold the different educations.
 *
 * @author Mikko Majapuro
 */
public class PreferenceRow extends Question {

    // label text for reset button
    private I18nText resetLabel;
    // label text for education drop down select
    private I18nText educationLabel;
    // label text for learning institution input (Opetuspiste)
    private I18nText learningInstitutionLabel;
    // place holder text for education select
    private String selectEducationPlaceholder;

    private String learningInstitutionInputId;
    private String educationInputId;

    public PreferenceRow(@JsonProperty(value = "id") final String id,
                         @JsonProperty(value = "i18nText") final I18nText i18nText,
                         @JsonProperty(value = "resetLabel") final I18nText resetLabel,
                         @JsonProperty(value = "educationLabel") final I18nText educationLabel,
                         @JsonProperty(value = "learningInstitutionLabel") final I18nText learningInstitutionLabel,
                         @JsonProperty(value = "selectEducationPlaceholder") final String selectEducationPlaceholder) {
        super(id, i18nText);
        this.resetLabel = resetLabel;
        this.educationLabel = educationLabel;
        this.learningInstitutionLabel = learningInstitutionLabel;
        this.selectEducationPlaceholder = selectEducationPlaceholder;
        this.learningInstitutionInputId = this.id + "-Opetuspiste";
        this.educationInputId = this.id + "-Koulutus";
    }

    public I18nText getResetLabel() {
        return resetLabel;
    }

    public I18nText getEducationLabel() {
        return educationLabel;
    }

    public I18nText getLearningInstitutionLabel() {
        return learningInstitutionLabel;
    }

    public String getSelectEducationPlaceholder() {
        return selectEducationPlaceholder;
    }

    public String getLearningInstitutionInputId() {
        return learningInstitutionInputId;
    }

    public String getEducationInputId() {
        return educationInputId;
    }

    @JsonIgnore
    public String getEducationOidInputId() {
        return educationInputId + "-id";
    }

    @Override
    public void addAttribute(final String key, final String value) {
        if ("required".equals(key)) {
            addValidator(new RequiredFieldFieldValidator(learningInstitutionInputId));
            addValidator(new RequiredFieldFieldValidator(educationInputId));
        } else {
            super.addAttribute(key, value);
        }
    }
}
