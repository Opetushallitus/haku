package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;


public class ThemeQuestionOption {

    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "optionText")
    private I18nText optionText;

    @JsonCreator
    public ThemeQuestionOption(){

    }

    public ThemeQuestionOption(String id,  I18nText optionText){
        this.id = id;
        this.optionText = optionText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public I18nText getOptionText() {
        return optionText;
    }

    public void setOptionText(I18nText optionText) {
        this.optionText = optionText;
    }
}
