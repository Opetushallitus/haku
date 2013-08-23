package fi.vm.sade.oppija.lomake.domain.rules.expression;

import com.google.common.collect.ImmutableMap;
import com.mongodb.util.JSON;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = AndOperator.class),
                @JsonSubTypes.Type(value = EqualsOperator.class),
                @JsonSubTypes.Type(value = OrOperator.class),
                @JsonSubTypes.Type(value = Value.class),
                @JsonSubTypes.Type(value = Variable.class),
        }
)
public abstract class Expr {
    final Expr left;
    final String op;
    final Expr right;
    private final String value;

    public Expr(final Expr left, final String op, final Expr right, String value) {
        this.left = left;
        this.op = op;
        this.right = right;
        this.value = value;
    }

    public abstract boolean evaluate(final Map<String, String> context);

    public String getValue(final Map<String, String> context) {
        return value;
    }

    public static void main(String[] args) {
        Expr leftValue = new Variable("id");
        Expr rightValue = new Value("10");
        Expr op = new EqualsOperator(leftValue, rightValue);
        System.out.println(op.evaluate(ImmutableMap.of("id", "10")));

        System.out.println(JSON.serialize(op));
    }
}
