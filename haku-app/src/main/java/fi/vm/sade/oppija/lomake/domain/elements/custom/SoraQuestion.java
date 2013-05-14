package fi.vm.sade.oppija.lomake.domain.elements.custom;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Question;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class SoraQuestion extends Question {

    private static final long serialVersionUID = 1668289645929978909L;

    private List<Radio> questions;


    public SoraQuestion(@JsonProperty(value = "id") String id,
                        @JsonProperty(value = "i18nText") I18nText i18nText,
                        @JsonProperty(value = "soraQuestions") List<Radio> questions) {
        super(id, i18nText);
        this.questions = questions;
    }

    public List<Radio> getQuestions() {
        return questions;
    }

}
