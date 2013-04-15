package fi.vm.sade.oppija.lomake.domain.elements.custom.gradegrid;

import fi.vm.sade.oppija.lomake.domain.elements.Element;
import org.codehaus.jackson.annotate.JsonProperty;

public class GradeGridAdditionalLanguage extends Element {

    public GradeGridAdditionalLanguage(@JsonProperty(value = "id") String id) {
        super(id);
    }
}
