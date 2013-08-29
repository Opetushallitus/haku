package fi.vm.sade.oppija.lomake.domain.rules.expression;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = And.class),
                @JsonSubTypes.Type(value = Equals.class),
                @JsonSubTypes.Type(value = Or.class),
                @JsonSubTypes.Type(value = Not.class),
                @JsonSubTypes.Type(value = Value.class),
                @JsonSubTypes.Type(value = Variable.class),
        }
)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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
