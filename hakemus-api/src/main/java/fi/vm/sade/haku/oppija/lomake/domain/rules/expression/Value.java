package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Value extends Expr {
    private static final String TRUE_STR = Boolean.TRUE.toString();
    public static final Value TRUE = new Value(TRUE_STR);
    public static final Value FALSE = new Value(Boolean.FALSE.toString());

    private final String value;

    public Value(final String value) {
        Preconditions.checkNotNull(value);
        this.value = value;
    }

    public String getValue(final Map<String, String> context) {
        return value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public List<Expr> children() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public boolean evaluate(final Map<String, String> context) {
        return TRUE_STR.equals(getValue(context));
    }
}
