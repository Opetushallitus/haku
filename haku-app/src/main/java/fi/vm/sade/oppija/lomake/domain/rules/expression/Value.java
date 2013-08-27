package fi.vm.sade.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

public class Value extends Expr {

    private static final String TRUE_STR = Boolean.TRUE.toString();
    public static final Value TRUE = new Value(TRUE_STR);
    public static final Value FALSE = new Value(Boolean.FALSE.toString());

    public Value(final String value) {
        super(null, null, value);
        Preconditions.checkNotNull(value);
    }

    @Override
    public boolean evaluate(final Map<String, String> context) {
        return TRUE_STR.equals(getValue(context));
    }
}
