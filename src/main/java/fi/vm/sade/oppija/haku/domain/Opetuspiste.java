package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Mikko Majapuro
 */
public class Opetuspiste implements Serializable {

    private static final long serialVersionUID = 3780799141790242873L;

    private String id;
    private String name;
    private String key;

    public Opetuspiste() {

    }

    public Opetuspiste(@JsonProperty(value = "id") final String id, @JsonProperty(value = "name") final String name) {
        this.id = id;
        this.name = name;
        this.key = name.toLowerCase(Locale.getDefault());
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
