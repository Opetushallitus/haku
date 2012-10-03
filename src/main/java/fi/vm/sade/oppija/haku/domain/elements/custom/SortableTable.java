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

    public SortableTable(@JsonProperty(value = "id") String id, @JsonProperty(value = "title") String title) {
        super(id, title);
    }
}
