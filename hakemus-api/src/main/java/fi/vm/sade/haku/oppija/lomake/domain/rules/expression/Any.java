package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import java.util.List;
import java.util.Map;

public class Any extends Expr {
    private final List<? extends Expr> exprs;

    public Any(final List<? extends Expr> exprs) {
        this.exprs = exprs;
    }

    @Override
    public boolean evaluate(final Map<String, String> context) {
        for (Expr child: exprs) {
            if (child.evaluate(context)) return true;
        }
        return false;
    }

    @Override
    public List<Expr> children() {
        return (List)exprs;
    }
}
