package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

public abstract class IntegerComparison extends Expr {

    public IntegerComparison(final Variable left, final Value right) {
        super(left, right, null);
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);
        Integer.parseInt(right.getValue());
    }

    protected abstract boolean evaluate(int left, int right);

    @Override
    public boolean evaluate(final Map<String, String> context) {
        String leftValue = getLeft().getValue(context);
        String rightValue = getRight().getValue(context);
        int left = Integer.parseInt(leftValue);
        int right = Integer.parseInt(rightValue);

        return evaluate(left, right);

    }
}
