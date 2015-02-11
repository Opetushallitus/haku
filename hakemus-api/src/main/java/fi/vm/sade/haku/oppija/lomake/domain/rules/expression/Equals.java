package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

public class Equals extends BinaryExpr {

    public Equals(final Expr left, final Expr right) {
        super(left, right);
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
