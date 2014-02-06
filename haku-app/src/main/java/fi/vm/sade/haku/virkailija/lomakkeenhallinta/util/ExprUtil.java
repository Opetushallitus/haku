package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.google.common.base.Preconditions;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;

import java.util.List;

public final class ExprUtil {
    //
    // Refactor to use Operator factory or in the future high order function.
    //
    public static Expr atLeastOneVariableEqualsToValue(final String value, final String... ids) {
        if (ids.length == 1) {
            return new Equals(new Variable(ids[0]), new Value(value));
        } else {
            Expr current = null;
            Expr equal;
            for (String id : ids) {
                equal = new Equals(new Variable(id), new Value(value));
                if (current == null) {
                    current = new Equals(new Variable(id), new Value(value));
                } else {
                    current = new Or(current, equal);
                }
            }
            return current;
        }
    }

    public static Expr atLeastOneValueEqualsToVariable(final String variable, final String... values) {
        if (values.length == 1) {
            return new Equals(new Value(values[0]), new Variable(variable));
        } else {
            Expr current = null;
            Expr equal;
            for (String value : values) {
                equal = new Equals(new Variable(variable), new Value(value));
                if (current == null) {
                    current = equal;
                } else {
                    current = new Or(current, equal);
                }
            }
            return current;
        }
    }

    public static Expr reduceToOr(final List<Expr> exprs) {
        Preconditions.checkArgument(!exprs.isEmpty());
        if (exprs.size() == 1) {
            return exprs.get(0);
        } else {
            Expr result = null;
            for (Expr expr : exprs) {
                if (result == null) {
                    result = expr;
                } else {
                    result = new Or(result, expr);
                }
            }
            return result;
        }
    }
}
