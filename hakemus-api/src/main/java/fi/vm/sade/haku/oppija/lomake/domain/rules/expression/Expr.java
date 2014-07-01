package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import java.util.Map;

public abstract class Expr {

    private final Expr left;
    private final Expr right;
    private final String value;

    public Expr(final Expr left, final Expr right, String value) {
        this.left = left;
        this.right = right;
        this.value = value;
    }

    public abstract boolean evaluate(final Map<String, String> context);

    public String getValue(final Map<String, String> context) {
        return value;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }

    public String getValue() {
        return value;
    }
}
