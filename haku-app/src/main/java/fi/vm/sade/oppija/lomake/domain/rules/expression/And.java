package fi.vm.sade.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

public class And extends Expr {
    public And(Expr left, Expr right) {
        super(left, right, null);
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        return getLeft().evaluate(context) && getRight().evaluate(context);
    }
}
