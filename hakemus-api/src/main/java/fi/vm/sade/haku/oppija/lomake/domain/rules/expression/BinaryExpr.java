package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BinaryExpr extends Expr {
    private final Expr left;
    private final Expr right;

    public BinaryExpr(final Expr left, final Expr right) {
        this.left = left;
        this.right = right;
    }

    public abstract boolean evaluate(final Map<String, String> context);

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }

    @Override
    public List<Expr> children() {
        return Arrays.asList(left, right);
    }

}
