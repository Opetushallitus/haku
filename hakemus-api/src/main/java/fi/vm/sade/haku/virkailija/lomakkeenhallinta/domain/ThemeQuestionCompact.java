package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ThemeQuestionCompact {

    private String type;
    private String messageText;
    private Map<String, String> options;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static ThemeQuestionCompact convert(ThemeQuestion question, String lang) {
        ThemeQuestionCompact questionCompact = new ThemeQuestionCompact();

        questionCompact.setMessageText(question.getMessageText().getText(lang));
        questionCompact.setType(question.getClass().getSimpleName());

        if (question instanceof ThemeOptionQuestion) {
            questionCompact.setOptions(convertOptions((ThemeOptionQuestion) question, lang));
        }

        return questionCompact;
    }

    public static Map<String, String> convertOptions(ThemeOptionQuestion optionQuestion, String lang) {
        Map<String, String> options = new HashMap<>();

        for (ThemeQuestionOption option : optionQuestion.getOptions()) {
            options.put(option.getId(), option.getOptionText().getText(lang));
        }

        return options;
    }

}
