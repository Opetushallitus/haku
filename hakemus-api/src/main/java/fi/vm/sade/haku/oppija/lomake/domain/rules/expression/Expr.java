package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import java.util.List;
import java.util.Map;

public abstract class Expr {
    public abstract boolean evaluate(final Map<String, String> context);

    public String getValue(final Map<String, String> context) {
        return null;
    }

    public String getValue() {
        return null;
    }

    public abstract List<Expr> children();
}
