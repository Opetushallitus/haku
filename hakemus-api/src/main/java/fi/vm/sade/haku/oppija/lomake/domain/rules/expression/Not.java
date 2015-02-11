package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Not extends Expr {
    private final Expr left;

    public Not(final Expr left) {
        this.left = left;
        Preconditions.checkNotNull(left);
    }

    @Override
    public List<Expr> children() {
        return Arrays.asList(left);
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        return !left.evaluate(context);
    }
}
