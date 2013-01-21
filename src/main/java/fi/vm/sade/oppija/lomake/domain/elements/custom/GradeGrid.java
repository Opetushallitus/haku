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

import fi.vm.sade.oppija.lomake.domain.elements.Titled;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Grid element that is used to gather grade information from user.
 *
 * @author Hannu Lyytikainen
 */
public class GradeGrid extends Titled {

    // label shown in custom language row ('Kieli' etc)
    private String customLanguageTitle;

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
                     @JsonProperty(value = "customLanguageTitle") String customLanguageTitle,
                     @JsonProperty(value = "subjectsBeforeLanguages") List<SubjectRow> subjectsBeforeLanguages,
                     @JsonProperty(value = "languages") List<LanguageRow> languages,
                     @JsonProperty(value = "subjectsAfterLanguages") List<SubjectRow> subjectsAfterLanguages,
                     @JsonProperty(value = "scopeOptions") List<Option> scopeOptions,
                     @JsonProperty(value = "languageOptions") List<Option> languageOptions,
                     @JsonProperty(value = "gradeRange") List<Option> gradeRange) {
        super(id, title);
        this.customLanguageTitle = customLanguageTitle;
        this.subjectsBeforeLanguages = subjectsBeforeLanguages;
        this.languages = languages;
        this.subjectsAfterLanguages = subjectsAfterLanguages;
        this.scopeOptions = scopeOptions;
        this.languageOptions = languageOptions;
        this.gradeRange = gradeRange;
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

}
