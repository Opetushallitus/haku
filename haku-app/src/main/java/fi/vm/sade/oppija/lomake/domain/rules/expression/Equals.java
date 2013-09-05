package fi.vm.sade.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

public class Equals extends Expr {

    public Equals(final Expr left, final Expr right) {
        super(left, right, null);
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
    }

    @Override
    public boolean evaluate(final Map<String, String> context) {
        String leftValue = getLeft().getValue(context);
        String rightValue = getRight().getValue(context);
        if (leftValue == null) {
            return rightValue == null;
        }
        return leftValue.equals(rightValue);
    }
}
