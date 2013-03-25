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
import fi.vm.sade.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomake.validation.Validator;
import fi.vm.sade.oppija.lomake.validation.validators.RequiredFieldFieldValidator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid element that is used to gather grade information from user.
 *
 * @author Hannu Lyytikainen
 */
public class GradeGrid extends Titled {

    private static final long serialVersionUID = 7703132498783434771L;

    // label shown in custom language row ('Kieli' etc)
    private String customLanguageTitle;

    // subjects that are listed before languages
    private List<SubjectRow> subjectsBeforeLanguages;

    private List<LanguageRow> languages;
    // subjects that are listed under the 'Add language' row
    private List<SubjectRow> subjectsAfterLanguages;

    // possible language scopes (A1, B1 etc)
    private List<Option> scopeOptions;
    // different languages
    private List<Option> languageOptions;
    // list of possible grades
    private List<Option> gradeRange;

    private boolean extraOptionalGrades;

    public GradeGrid(@JsonProperty(value = "id") final String id,
                     @JsonProperty(value = "i18nText") final I18nText i18nText,
                     @JsonProperty(value = "customLanguageTitle") final String customLanguageTitle,
                     @JsonProperty(value = "subjectsBeforeLanguages") final List<SubjectRow> subjectsBeforeLanguages,
                     @JsonProperty(value = "languages") final List<LanguageRow> languages,
                     @JsonProperty(value = "subjectsAfterLanguages") final List<SubjectRow> subjectsAfterLanguages,
                     @JsonProperty(value = "scopeOptions") final List<Option> scopeOptions,
                     @JsonProperty(value = "languageOptions") final List<Option> languageOptions,
                     @JsonProperty(value = "gradeRange") final List<Option> gradeRange,
                     @JsonProperty(value = "extraOptionalGrades") final boolean extraOptionalGrades) {
        super(id, i18nText);
        this.customLanguageTitle = customLanguageTitle;
        this.subjectsBeforeLanguages = subjectsBeforeLanguages;
        this.languages = languages;
        this.subjectsAfterLanguages = subjectsAfterLanguages;
        this.scopeOptions = scopeOptions;
        this.languageOptions = languageOptions;
        this.gradeRange = gradeRange;
        this.extraOptionalGrades = extraOptionalGrades;
    }

    public List<Option> getGradeRange() {
        return gradeRange;
    }

    public List<SubjectRow> getSubjectsBeforeLanguages() {
        return subjectsBeforeLanguages;
    }

    public List<LanguageRow> getLanguages() {
        return languages;
    }

    public List<SubjectRow> getSubjectsAfterLanguages() {
        return subjectsAfterLanguages;
    }

    public List<Option> getScopeOptions() {
        return scopeOptions;
    }

    public List<Option> getLanguageOptions() {
        return languageOptions;
    }

    public String getCustomLanguageTitle() {
        return customLanguageTitle;
    }

    public boolean isExtraOptionalGrades() {
        return extraOptionalGrades;
    }

    @Override
    @JsonIgnore
    public List<Validator> getValidators() {
        List<Validator> listOfValidators = new ArrayList<Validator>();
        for (SubjectRow subjectRow : subjectsBeforeLanguages) {
            addRequiredValidators(listOfValidators, subjectRow);
        }
        for (LanguageRow languageRow : languages) {
            addRequiredValidators(listOfValidators, languageRow);
        }
        for (SubjectRow subjectRow : subjectsAfterLanguages) {
            addRequiredValidators(listOfValidators, subjectRow);
        }
        return listOfValidators;
    }

    private void addRequiredValidators(final List<Validator> listOfValidators, final SubjectRow subjectRow) {
        String subjectRowId = subjectRow.getId();
        listOfValidators.add(new RequiredFieldFieldValidator("common-" + subjectRowId));
        listOfValidators.add(new RequiredFieldFieldValidator("optional-common-" + subjectRowId));
        if (this.extraOptionalGrades) {
            listOfValidators.add(new RequiredFieldFieldValidator("second-optional-common-" + subjectRowId));
        }
    }
}
