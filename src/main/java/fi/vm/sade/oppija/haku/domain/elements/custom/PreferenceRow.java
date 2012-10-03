package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.questions.OptionQuestion;

/**
 * Renders as a user's application preference row. Title is used to hold the name of the preference row (Hakutoive 1, Hakutoive 2 etc.)
 * Options are used to hold the different educations.
 *
 * @author Mikko Majapuro
 */
public class PreferenceRow extends OptionQuestion {

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
}
