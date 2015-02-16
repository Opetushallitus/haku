package fi.vm.sade.haku.virkailija.lomakkeenhallinta.util;

import com.google.common.base.Preconditions;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.*;

import java.util.ArrayList;
import java.util.List;

public final class ExprUtil {
    public static Expr equals(final String variable, final String value) {
        return new Equals(new Variable(variable), new Value(value));
    }

    public static Expr atLeastOneVariableEqualsToValue(final String value, final String... ids) {
        List<Expr> exprs = new ArrayList<>();
        for (String id : ids) {
            Equals equal = new Equals(new Variable(id), new Value(value));
            exprs.add(equal);
        }
        return any(exprs);
    }

    public static Expr atLeastOneVariableContainsValue(final String value, final String... ids) {
        List<Expr> exprs = new ArrayList<>();
        for (String id : ids) {
            Regexp rexExp = new Regexp(id, "(?:.*\\s*,\\s*|\\s*)" + value + "(?:,.*|\\s*|\\s+.*)");
            exprs.add(rexExp);
        }
        return any(exprs);
    }

    public static Expr atLeastOneValueEqualsToVariable(final String variable, final String... values) {
        List<Expr> exprs = new ArrayList<>();
        for (String value : values) {
            Equals equal = new Equals(new Variable(variable), new Value(value));
            exprs.add(equal);
        }
        return any(exprs);
    }

    public static Expr any(final List<? extends Expr> exprs) {
        Preconditions.checkArgument(!exprs.isEmpty());
        if (exprs.size() == 1) {
            return exprs.get(0);
        } else {
            return new Any(exprs);
        }
    }

    public static Expr isAnswerTrue(final String id) {
        return new Equals(new Variable(id), Value.TRUE);
    }

    public static Expr lessThanRule(final String thisIsSmaller, final String whenComparedToThis) {
        return new LessThan(new Variable(thisIsSmaller), new Value(whenComparedToThis));
    }
}
