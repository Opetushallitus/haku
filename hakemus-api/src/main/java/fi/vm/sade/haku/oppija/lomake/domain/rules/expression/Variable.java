package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

public class Variable extends Value {

    public Variable(final String value) {
        super(value);
        Preconditions.checkNotNull("Variable name can not be null", value);
    }

    @Override
    public String getValue(final Map<String, String> context) {
        return context.get(super.getValue(context));
    }
}

