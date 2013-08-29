package fi.vm.sade.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class Variable extends Value {

    public Variable(@JsonProperty(value = "value") final String name) {
        super(name);
        Preconditions.checkNotNull("Variable name can not be null", name);
    }

    @Override
    public String getValue(final Map<String, String> context) {
        return context.get(super.getValue(context));
    }
}

