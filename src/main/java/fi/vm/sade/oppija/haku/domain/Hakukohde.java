package fi.vm.sade.oppija.haku.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.custom.SubjectRow;
import fi.vm.sade.oppija.haku.domain.questions.Question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class Hakukohde implements Serializable {

    private String id;
    private String name;

    private List<Question> lisakysymysList;
    private List<SubjectRow> oppiaineList;

    public Hakukohde(@JsonProperty(value = "id")String id, @JsonProperty(value = "name")String name,
                     @JsonProperty(value = "lisakysymysList")List<Question> lisakysymysList,
                     @JsonProperty(value = "oppiaineList")List<SubjectRow> oppiaineList) {
        this.id = id;
        this.name = name;
        this.lisakysymysList = lisakysymysList;
        this.oppiaineList = oppiaineList;
    }

    public Hakukohde(@JsonProperty(value = "id")String id, @JsonProperty(value = "name")String name) {
        this.id = id;
        this.name = name;
        this.lisakysymysList = new ArrayList<Question>();
        this.oppiaineList = new ArrayList<SubjectRow>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Question> getLisakysymysList() {
        return lisakysymysList;
    }

    public List<SubjectRow> getOppiaineList() {
        return oppiaineList;
    }
}
