package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Titled;

import java.util.List;

/**
 * Grid element that is used to gather grade information from user. Child elements
 * are used to list different subjects in the grid.
 *
 * @author Hannu Lyytikainen
 */
public class GradeGrid extends Titled {

    private String registryGradesTitle;
    private String alteringGradesTitle;
    private String gradesTitle;
    private String commonSubjectColumnTitle;
    private String optionalSubjectColumnTitle;
    private List<Integer> gradeRange;

    public GradeGrid(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                     @JsonProperty(value = "registryGradesTitle") String registryGradesTitle,
                     @JsonProperty(value = "alteringGradesTitle") String alteringGradesTitle,
                     @JsonProperty(value = "gradesTitle") String gradesTitle,
                     @JsonProperty(value = "commonSubjectColumnTitle") String commonSubjectColumnTitle,
                     @JsonProperty(value = "optionalSubjectColumnTitle") String optionalSubjectColumnTitle,
                     @JsonProperty(value = "gradeRange") List<Integer> gradeRange) {
        super(id, title);
        this.registryGradesTitle = registryGradesTitle;
        this.alteringGradesTitle = alteringGradesTitle;
        this.gradesTitle = gradesTitle;
        this.commonSubjectColumnTitle = commonSubjectColumnTitle;
        this.optionalSubjectColumnTitle = optionalSubjectColumnTitle;
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
}
