package fi.vm.sade.oppija.lomake.domain.rules;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

public class IfRule extends Rule {

    public IfRule(@JsonProperty(value = "relatedElementId") List<String> relatedElementId) {
        super(relatedElementId);
    }

    @Override
    public boolean evaluate(Map<String, String> values) {
        return true;
    }


}
