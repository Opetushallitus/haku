package fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid;

import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class GradeGridOptionQuestion extends Element {


    private final List<Option> options;
    private final boolean selected;

    public GradeGridOptionQuestion(@JsonProperty(value = "id") final String id,
                                   @JsonProperty(value = "gradeRange") final List<Option> gradeRange,
                                   @JsonProperty(value = "selected") final boolean selected) {
        super(id);
        addAttribute("name", id);
        this.options = gradeRange;
        this.selected = selected;
    }

    public List<Option> getOptions() {
        return options;
    }

    public boolean isSelected() {
        return selected;
    }
}
