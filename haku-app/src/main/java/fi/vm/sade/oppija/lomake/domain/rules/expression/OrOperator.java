package fi.vm.sade.oppija.lomake.domain.rules.expression;

import java.util.Map;

public class OrOperator extends Expr {
    public OrOperator(Expr left, Expr right) {
        super(left, "or", right, null);
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        return this.left.evaluate(context) || this.right.evaluate(context);
    }
}
