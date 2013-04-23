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

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import fi.vm.sade.oppija.lomake.validation.Validator;
import fi.vm.sade.oppija.lomake.validation.validators.PreferenceRowValidator;
import fi.vm.sade.oppija.lomake.validation.validators.RequiredFieldFieldValidator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders as a user's application preference row. Title is used to hold the name of the preference row (Hakutoive 1, Hakutoive 2 etc.)
 * Options are used to hold the different educations.
 *
 * @author Mikko Majapuro
 */
public class PreferenceRow extends Question {

    private static final long serialVersionUID = 5149303147942411002L;
    // label text for reset button
    private I18nText resetLabel;
    // label text for education drop down select
    private I18nText educationLabel;
    // label text for learning institution input (Opetuspiste)
    private I18nText learningInstitutionLabel;
    // label text for the child learning opportunity list
    private I18nText childLONameListLabel;
    // place holder text for education select
    private String selectEducationPlaceholder;
    private String learningInstitutionInputId;
    private String educationInputId;
    private String educationDegreeId;
    // application option with this education degree leads into a discretionary question
    private Integer discretionaryEducationDegree;
    // question that is asked when user applies for an application option with a specific education degree
    private DiscretionaryQuestion discretionaryQuestion;

    public PreferenceRow(@JsonProperty(value = "id") final String id,
                         @JsonProperty(value = "i18nText") final I18nText i18nText,
                         @JsonProperty(value = "resetLabel") final I18nText resetLabel,
                         @JsonProperty(value = "educationLabel") final I18nText educationLabel,
                         @JsonProperty(value = "learningInstitutionLabel") final I18nText learningInstitutionLabel,
                         @JsonProperty(value = "childLONameListLabel") final I18nText childLONameListLabel,
                         @JsonProperty(value = "selectEducationPlaceholder") final String selectEducationPlaceholder,
                         @JsonProperty(value = "discretionaryEducationDegree") final Integer discretionaryEducationDegree,
                         @JsonProperty(value = "discretionaryQuestion") final DiscretionaryQuestion discretionaryQuestion
                         ) {
        super(id, i18nText);
        this.resetLabel = resetLabel;
        this.educationLabel = educationLabel;
        this.learningInstitutionLabel = learningInstitutionLabel;
        this.childLONameListLabel = childLONameListLabel;
        this.selectEducationPlaceholder = selectEducationPlaceholder;
        this.discretionaryEducationDegree = discretionaryEducationDegree;
        this.discretionaryQuestion = discretionaryQuestion;
        this.learningInstitutionInputId = this.id + "-Opetuspiste";
        this.educationInputId = this.id + "-Koulutus";
        this.educationDegreeId = this.id + "-Koulutus-educationDegree";
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

    public I18nText getChildLONameListLabel() {
        return childLONameListLabel;
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

    public String getEducationDegreeId() {
        return educationDegreeId;
    }

    public DiscretionaryQuestion getDiscretionaryQuestion() {
        return discretionaryQuestion;
    }

    public Integer getDiscretionaryEducationDegree() {
        return discretionaryEducationDegree;
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

    @Override
    public List<Validator> getValidators() {
        List<Validator> validatroList = new ArrayList<Validator>();
        PreferenceRowValidator validator = new PreferenceRowValidator(this.educationDegreeId,
                this.discretionaryEducationDegree.toString(), this.discretionaryQuestion.getId());
        validatroList.add(validator);
        return validatroList;
    }
}
