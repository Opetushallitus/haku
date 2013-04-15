package fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid;

import fi.vm.sade.oppija.lomake.domain.elements.Element;
import org.codehaus.jackson.annotate.JsonProperty;

public class GradeGridColumn extends Element {

    private final boolean removable;

    public GradeGridColumn(@JsonProperty(value = "id") String id,
                           @JsonProperty(value = "removable") final boolean removable) {
        super(id);
        this.removable = removable;
    }

    public boolean isRemovable() {
        return removable;
    }
}
