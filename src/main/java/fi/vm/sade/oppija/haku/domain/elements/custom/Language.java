package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class Language extends Subject {

    private List<String> scope;

    public Language(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                    @JsonProperty(value = "scope") List<String> scope) {
        super(id, title);
        this.scope = scope;
    }
}
