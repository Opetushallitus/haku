package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Titled;
import fi.vm.sade.oppija.haku.domain.questions.Option;

import java.util.List;

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
    // title for common subject grade column (Yleinen oppiaine etc)
    private String commonSubjectColumnTitle;
    // title for optional subject grade column (Valinnaisaine etc)
    private String optionalSubjectColumnTitle;

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
    private List<Integer> gradeRange;

    public GradeGrid(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                     @JsonProperty(value = "registryGradesTitle") String registryGradesTitle,
                     @JsonProperty(value = "alteringGradesTitle") String alteringGradesTitle,
                     @JsonProperty(value = "gradesTitle") String gradesTitle,
                     @JsonProperty(value = "commonSubjectColumnTitle") String commonSubjectColumnTitle,
                     @JsonProperty(value = "optionalSubjectColumnTitle") String optionalSubjectColumnTitle,
                     @JsonProperty(value = "subjectsBeforeLanguages") List<SubjectRow> subjectsBeforeLanguages,
                     @JsonProperty(value = "languages") List<LanguageRow> languages,
                     @JsonProperty(value = "subjectsAfterLanguages") List<SubjectRow> subjectsAfterLanguages,
                     @JsonProperty(value = "scopeOptions") List<Option> scopeOptions,
                     @JsonProperty(value = "languageOptions") List<Option> languageOptions,
                     @JsonProperty(value = "gradeRange") List<Integer> gradeRange) {
        super(id, title);
        this.registryGradesTitle = registryGradesTitle;
        this.alteringGradesTitle = alteringGradesTitle;
        this.gradesTitle = gradesTitle;
        this.commonSubjectColumnTitle = commonSubjectColumnTitle;
        this.optionalSubjectColumnTitle = optionalSubjectColumnTitle;
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

    public List<Integer> getGradeRange() {
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
}
