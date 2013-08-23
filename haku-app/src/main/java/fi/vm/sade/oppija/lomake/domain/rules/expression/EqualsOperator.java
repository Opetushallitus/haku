package fi.vm.sade.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

public final class EqualsOperator extends Expr {

    public EqualsOperator(final Expr left, final Expr right) {
        super(left, "=", right, null);
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
    }

    @Override
    public boolean evaluate(final Map<String, String> context) {
        if (this.left.getValue(context) == null) {
            return this.right.getValue(context) == null;
        }
        return this.left.getValue(context).equals(this.right.getValue(context));
    }
}
