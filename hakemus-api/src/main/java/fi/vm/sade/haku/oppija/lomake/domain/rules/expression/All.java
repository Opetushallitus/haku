package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Evaluates all children separately, in order to yield true all children must evaluate to true.
 * No parameters evaluates to true.
 */
public class All extends Expr {
    private final List<Expr> expressions;

    public All(final Expr... expressions) {
        Preconditions.checkNotNull(expressions);
        for (Expr item : expressions) {
            Preconditions.checkNotNull(item);
        }
        this.expressions = Arrays.asList(expressions);
    }


    @Override
    public boolean evaluate(Map<String, String> context) {
        for (Expr child: expressions) {
            if (! child.evaluate(context)) return false;
        }
        return true;
    }

    @Override
    public List<Expr> children() {
        return expressions;
    }

}
