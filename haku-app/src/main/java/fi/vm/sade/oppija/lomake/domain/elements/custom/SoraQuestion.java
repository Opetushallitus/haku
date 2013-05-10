package fi.vm.sade.oppija.lomake.domain.elements.custom;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;

public class SoraQuestion extends Question {

    private static final long serialVersionUID = 1668289645929978909L;
    
    private boolean soraRequired;
    private List<Radio> questions;
    
    
    public SoraQuestion(@JsonProperty(value = "id") String id, 
        @JsonProperty(value = "i18nText") I18nText i18nText, 
        @JsonProperty(value = "soraQuestions") List<Radio> questions,
        @JsonProperty(value = "soraRequired") boolean soraRequired) {
        super(id, i18nText);
        this.questions = questions;
        this.soraRequired = soraRequired;
    }

    public List<Radio> getQuestions() {
        return questions;
    }
    
    public boolean isSoraRequired() {
        return this.soraRequired;
    }
    
    public void addQuestion(Radio question) {
        this.questions.add(question);
    }

}
