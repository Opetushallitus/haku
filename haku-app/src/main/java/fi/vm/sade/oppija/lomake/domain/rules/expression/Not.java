package fi.vm.sade.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

public class Not extends Expr {
    public Not(final Expr expr) {
        super(expr, null, null);
        Preconditions.checkNotNull(expr);
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        return !getLeft().evaluate(context);
    }
}
