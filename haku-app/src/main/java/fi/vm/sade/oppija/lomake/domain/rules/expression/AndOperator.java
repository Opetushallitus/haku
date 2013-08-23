package fi.vm.sade.oppija.lomake.domain.rules.expression;

import java.util.Map;

public class AndOperator extends Expr {
    public AndOperator(Expr left, Expr right) {
        super(left, "and", right, null);
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        return this.left.evaluate(context) && this.right.evaluate(context);
    }
}
