package fi.vm.sade.haku.oppija.lomake.domain.builder;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.haku.oppija.lomake.domain.rules.expression.Expr;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;

public class RelatedQuestionRuleBuilder extends ElementBuilder {
    private Expr expr;

    protected RelatedQuestionRuleBuilder(final String id) {
        super(id);
    }

    public RelatedQuestionRuleBuilder setExpr(Expr expr) {
        this.expr = expr;
        return this;
    }

    @Override
    public Element buildImpl() {
        return new RelatedQuestionRule(id, expr);
    }

    public static RelatedQuestionRuleBuilder Rule(final String id) {
        return new RelatedQuestionRuleBuilder(id);
    }
    public static RelatedQuestionRuleBuilder Rule() {
        return new RelatedQuestionRuleBuilder(ElementUtil.randomId());
    }
    public static RelatedQuestionRuleBuilder Rule(final Expr expr) {
        return Rule().setExpr(expr);
    }
}
