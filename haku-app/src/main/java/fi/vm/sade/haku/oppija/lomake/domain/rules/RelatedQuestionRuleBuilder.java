package fi.vm.sade.haku.oppija.lomake.domain.rules;

import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;

public class RelatedQuestionRuleBuilder {
    private String id;
    private Expr expr;

    public RelatedQuestionRuleBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public RelatedQuestionRuleBuilder setExpr(Expr expr) {
        this.expr = expr;
        return this;
    }

    public RelatedQuestionRule createRelatedQuestionRule() {
        return new RelatedQuestionRule(id, expr);
    }
}
