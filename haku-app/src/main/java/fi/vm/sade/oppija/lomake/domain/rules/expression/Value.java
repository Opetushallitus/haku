package fi.vm.sade.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

public class Value extends Expr {
    public Value(String value) {
        super(null, null, null, value);
        Preconditions.checkNotNull(value);
    }

    @Override
    public boolean evaluate(final Map<String, String> context) {
        return false;
    }
}
