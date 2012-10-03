package fi.vm.sade.oppija.haku.domain.elements.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.vm.sade.oppija.haku.domain.elements.Titled;

/**
 * Table element with data sorting functionality
 *
 * @author Mikko Majapuro
 *
 */
public class SortableTable extends Titled {

    // label text for up button
    private String moveUpLabel;
    // label text for down button
    private String moveDownLabel;

    public SortableTable(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title,
                         @JsonProperty(value = "moveUpLabel") String moveUpLabel,
                         @JsonProperty(value = "moveDownLabel") String moveDownLabel) {
        super(id, title);
        this.moveUpLabel = moveUpLabel;
        this.moveDownLabel = moveDownLabel;
    }

    public String getMoveUpLabel() {
        return moveUpLabel;
    }

    public String getMoveDownLabel() {
        return moveDownLabel;
    }
}
