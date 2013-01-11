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

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.lomake.domain.Attribute;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.validation.validators.RequiredFieldFieldValidator;

import java.util.Map;

/**
 * Renders as a user's application preference row. Title is used to hold the name of the preference row (Hakutoive 1, Hakutoive 2 etc.)
 * Options are used to hold the different educations.
 *
 * @author Mikko Majapuro
 */
public class PreferenceRow extends Question {

    // label text for reset button
    private String resetLabel;
    // label text for education drop down select
    private String educationLabel;
    // label text for learning institution input (Opetuspiste)
    private String learningInstitutionLabel;
    // place holder text for education select
    private String selectEducationPlaceholder;

    public PreferenceRow(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title,
                         @JsonProperty(value = "resetLabel") String resetLabel,
                         @JsonProperty(value = "educationLabel") String educationLabel,
                         @JsonProperty(value = "learningInstitutionLabel") String learningInstitutionLabel,
                         @JsonProperty(value = "selectEducationPlaceholder") String selectEducationPlaceholder) {
        super(id, title);
        this.resetLabel = resetLabel;
        this.educationLabel = educationLabel;
        this.learningInstitutionLabel = learningInstitutionLabel;
        this.selectEducationPlaceholder = selectEducationPlaceholder;
    }

    public String getResetLabel() {
        return resetLabel;
    }

    public String getEducationLabel() {
        return educationLabel;
    }

    public String getLearningInstitutionLabel() {
        return learningInstitutionLabel;
    }

    public String getSelectEducationPlaceholder() {
        return selectEducationPlaceholder;
    }

    @Override
    public void initValidators() {
        for (Map.Entry<String, Attribute> attribute : attributes.entrySet()) {
            if (attribute.getKey().equals("required")) {
                this.validators.add(new RequiredFieldFieldValidator(this.id + "-Opetuspiste"));
                this.validators.add(new RequiredFieldFieldValidator(this.id + "-Koulutus"));
            }
        }
    }
}
