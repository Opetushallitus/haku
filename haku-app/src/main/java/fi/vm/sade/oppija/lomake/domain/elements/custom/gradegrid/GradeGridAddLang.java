package fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid;

import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class GradeGridAddLang extends Element {

    private List<Option> subjects;
    private List<Option> languages;
    private List<Option> grades;

    public GradeGridAddLang(@JsonProperty(value = "id") final String id,
                            @JsonProperty(value = "subjects") final List<Option> subjects,
                            @JsonProperty(value = "languages") final List<Option> languages,
                            @JsonProperty(value = "grades") final List<Option> grades) {
        super(id);
        this.subjects = subjects;
        this.languages = languages;
        this.grades = grades;
    }

    public List<Option> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Option> subjects) {
        this.subjects = subjects;
    }

    public List<Option> getGrades() {
        return grades;
    }

    public List<Option> getLanguages() {
        return languages;
    }
}
