package fi.vm.sade.oppija.lomake.domain.elements.custom;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Theme;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * @author Hannu Lyytikainen
 */
public class WorkExperienceTheme extends Theme {

    // Degree types of the requested application options
    private String[] aoEducationDegreeKeys = {"preference1-Koulutus-educationDegree", "preference2-Koulutus-educationDegree",
            "preference3-Koulutus-educationDegree", "preference4-Koulutus-educationDegree",
            "preference5-Koulutus-educationDegree"};
    // degree type that needs to be applied to
    // so that this phase is rendered
    private String requiredEducationDegree;

    public WorkExperienceTheme(@JsonProperty(value = "id") String id, @JsonProperty(value = "i18nText") I18nText i18nText,
                               @JsonProperty(value = "additionalQuestions") Map<String, List<Question>> additionalQuestions,
                               @JsonProperty(value = "requiredEducationDegree") String requiredEducationDegree) {

        super(id, i18nText, additionalQuestions);
        this.requiredEducationDegree = requiredEducationDegree;
    }

    public String[] getAoEducationDegreeKeys() {
        return aoEducationDegreeKeys;
    }

    public void setRequiredEducationDegree(String requiredEducationDegree) {
        this.requiredEducationDegree = requiredEducationDegree;
    }

    public String getRequiredEducationDegree() {
        return requiredEducationDegree;
    }

}
