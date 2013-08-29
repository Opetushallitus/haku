package fi.vm.sade.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class Or extends Expr {
    public Or(@JsonProperty(value = "left") final Expr left,
              @JsonProperty(value = "right") final Expr right) {
        super(left, right, null);
        Preconditions.checkNotNull(left);
        Preconditions.checkNotNull(right);

    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        return getLeft().evaluate(context) || getRight().evaluate(context);
    }
}
