package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Titled;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class GradeGrid extends Titled {

    private String registryGradesTitle;
    private String alteringGradesTitle;
    private String gradesTitle;
    private String commonSubjectColumnTitle;
    private String optionalSubjectColumnTitle;
    private List<Integer> gradeRange;
    private List<Subject> subjects;

    public GradeGrid(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                     @JsonProperty(value = "registryGradesTitle") String registryGradesTitle,
                     @JsonProperty(value = "alteringGradesTitle") String alteringGradesTitle,
                     @JsonProperty(value = "gradesTitle") String gradesTitle,
                     @JsonProperty(value = "commonSubjectColumnTitle") String commonSubjectColumnTitle,
                     @JsonProperty(value = "optionalSubjectColumnTitle") String optionalSubjectColumnTitle,
                     @JsonProperty(value = "gradeRange") List<Integer> gradeRange,
                     @JsonProperty(value = "subjects") List<Subject> subjects) {
        super(id, title);
        this.registryGradesTitle = registryGradesTitle;
        this.alteringGradesTitle = alteringGradesTitle;
        this.gradesTitle = gradesTitle;
        this.commonSubjectColumnTitle = commonSubjectColumnTitle;
        this.optionalSubjectColumnTitle = optionalSubjectColumnTitle;
        this.gradeRange = gradeRange;
        this.subjects = subjects;
    }


}
