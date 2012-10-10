package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author Hannu Lyytikainen
 */
public class Hakukohde implements Serializable {

    private String id;
    private String name;

    public Hakukohde(@JsonProperty(value = "id")String id, @JsonProperty(value = "name")String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
