package fi.vm.sade.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class Not extends Expr {
    public Not(final Expr left) {
        super(left, null, null);
        Preconditions.checkNotNull(left);
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        return !getLeft().evaluate(context);
    }
}
