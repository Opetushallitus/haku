package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author Mikko Majapuro
 */
public class Opetuspiste implements Serializable {
    private String id;
    private String name;

    public Opetuspiste() {

    }

    public Opetuspiste(@JsonProperty(value = "id") final String id, @JsonProperty(value = "name") final String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
