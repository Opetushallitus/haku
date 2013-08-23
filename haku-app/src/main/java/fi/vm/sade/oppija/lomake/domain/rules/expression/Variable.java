package fi.vm.sade.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

public class Variable extends Expr {
    public Variable(String name) {
        super(null, null, null, name);
        Preconditions.checkNotNull(name);
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        return false;
    }

    @Override
    public String getValue(Map<String, String> context) {
        return context.get(super.getValue(context));
    }
}

