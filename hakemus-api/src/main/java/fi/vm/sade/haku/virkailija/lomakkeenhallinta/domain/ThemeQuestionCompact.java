package fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ThemeQuestionCompact {

    private String type;
    private String messageText;
    private Map<String, String> options;
    private Set<String> applicationOptionOids = new HashSet<>();

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

    public Set<String> getApplicationOptionOids() {
        return applicationOptionOids;
    }

    public void setApplicationOptionOids(Set<String> applicationOptionOids) {
        this.applicationOptionOids = applicationOptionOids;
    }

}
