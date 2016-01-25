package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Returns true if the given value is not null
 */
public class NotNull extends Expr {

    private final Object value;

    public NotNull(final Object value) {
        this.value = value;
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        if (value != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Expr> children() {
        return Collections.EMPTY_LIST;
    }
}
