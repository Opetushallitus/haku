package fi.vm.sade.oppija.haku.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jukka
 * @version 9/7/1210:36 AM}
 * @since 1.1
 */
public abstract class Titled extends Element {

    final String title;

    public Titled(@JsonProperty(value = "id") final String id, @JsonProperty(value = "title") final String title) {
        super(id);
        this.title = title;
    }


    public String getTitle() {
        return title;
    }
}
