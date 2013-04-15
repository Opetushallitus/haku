package fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid;

import fi.vm.sade.oppija.lomake.domain.elements.Element;
import org.codehaus.jackson.annotate.JsonProperty;

public class GradeGridRow extends Element {

    public GradeGridRow(@JsonProperty(value = "id") String id) {
        super(id);
    }
}
