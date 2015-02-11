package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

public class And extends BinaryExpr {
    public And(final Expr left,
               final Expr right) {
        super(left, right);
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        return getLeft().evaluate(context) && getRight().evaluate(context);
    }
}
