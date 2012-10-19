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

package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Titled;
import fi.vm.sade.oppija.haku.domain.questions.Option;

import java.util.List;
import java.util.Set;

/**
 * Grid element that is used to gather grade information from user.
 *
 * @author Hannu Lyytikainen
 */
public class GradeGrid extends Titled {

    // title for column that holds grades retrieved from an external registry
    private String registryGradesTitle;
    // title for columns that are altered from the ones that are retrieved from a registry
    private String alteringGradesTitle;
    // title for grade columns if a grade registry is absent
    private String gradesTitle;
    // title for subject names column
    private String subjectTitle;
    // title for common subject grade column ('Yleinen oppiaine' etc)
    private String commonSubjectColumnTitle;
    // title for optional subject grade column ('Valinnaisaine' etc)
    private String optionalSubjectColumnTitle;
    // label shown in custom language row ('Kieli' etc)
    private String customLanguageTitle;
    // label text for add language button
    private String addLanguageLabel;

    // subjects that are listed before languages
    private List<SubjectRow> subjectsBeforeLanguages;
    // languages
    private List<LanguageRow> languages;
    // subjects that are listed under the 'Add language' row
    private List<SubjectRow> subjectsAfterLanguages;

    // possible language scopes (A1, B1 etc)
    private List<Option> scopeOptions;
    // different languages
    private List<Option> languageOptions;
    // list of possible grades
    private List<Option> gradeRange;

    public GradeGrid(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                     @JsonProperty(value = "registryGradesTitle") String registryGradesTitle,
                     @JsonProperty(value = "alteringGradesTitle") String alteringGradesTitle,
                     @JsonProperty(value = "gradesTitle") String gradesTitle,
                     @JsonProperty(value = "subjectTitle") String subjectTitle,
                     @JsonProperty(value = "commonSubjectColumnTitle") String commonSubjectColumnTitle,
                     @JsonProperty(value = "optionalSubjectColumnTitle") String optionalSubjectColumnTitle,
                     @JsonProperty(value = "customLanguageTitle") String customLanguageTitle,
                     @JsonProperty(value = "addLanguageLabel") String addLanguageLabel,
                     @JsonProperty(value = "subjectsBeforeLanguages") List<SubjectRow> subjectsBeforeLanguages,
                     @JsonProperty(value = "languages") List<LanguageRow> languages,
                     @JsonProperty(value = "subjectsAfterLanguages") List<SubjectRow> subjectsAfterLanguages,
                     @JsonProperty(value = "scopeOptions") List<Option> scopeOptions,
                     @JsonProperty(value = "languageOptions") List<Option> languageOptions,
                     @JsonProperty(value = "gradeRange") List<Option> gradeRange) {
        super(id, title);
        this.registryGradesTitle = registryGradesTitle;
        this.alteringGradesTitle = alteringGradesTitle;
        this.gradesTitle = gradesTitle;
        this.subjectTitle = subjectTitle;
        this.commonSubjectColumnTitle = commonSubjectColumnTitle;
        this.optionalSubjectColumnTitle = optionalSubjectColumnTitle;
        this.customLanguageTitle = customLanguageTitle;
        this.addLanguageLabel = addLanguageLabel;
        this.subjectsBeforeLanguages = subjectsBeforeLanguages;
        this.languages = languages;
        this.subjectsAfterLanguages = subjectsAfterLanguages;
        this.scopeOptions = scopeOptions;
        this.languageOptions = languageOptions;
        this.gradeRange = gradeRange;
    }

    public String getRegistryGradesTitle() {
        return registryGradesTitle;
    }

    public String getAlteringGradesTitle() {
        return alteringGradesTitle;
    }

    public String getGradesTitle() {
        return gradesTitle;
    }

    public String getCommonSubjectColumnTitle() {
        return commonSubjectColumnTitle;
    }

    public String getOptionalSubjectColumnTitle() {
        return optionalSubjectColumnTitle;
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

    public String getSubjectTitle() {
        return subjectTitle;
    }

    public String getCustomLanguageTitle() {
        return customLanguageTitle;
    }

    public String getAddLanguageLabel() {
        return addLanguageLabel;
    }

}
