package fi.vm.sade.haku.oppija.lomake.validation.validators;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.haku.oppija.lomake.validation.Validator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * Created by majapuro on 11.12.2014.
 */
public class ExprValidator implements Validator {

    private final Expr expr;
    private final I18nText errorMessage;


    @PersistenceConstructor
    public ExprValidator(@JsonProperty(value = "expr") Expr expr,
                         @JsonProperty(value = "errorMessage") I18nText errorMessage) {
        this.expr = expr;
        this.errorMessage = errorMessage;
    }

    @Override
    public ValidationResult validate(ValidationInput validationInput) {
        if (expr.evaluate(validationInput.getValues())) {
            return new ValidationResult();
        } else {
            return new ValidationResult(validationInput.getFieldName(), getErrorMessage());
        }
    }

    public Expr getExpr() {
        return expr;
    }

    public I18nText getErrorMessage() {
        return errorMessage;
    }
}
