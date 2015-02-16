package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ValueListContains extends Expr {

    private final List<String> valueList;
    private final Expr valueExpr;

    public ValueListContains(final List<String> valueList, final Expr valueExpr) {
        Preconditions.checkNotNull(valueList);
        for(String item: valueList) {
            Preconditions.checkNotNull(item);
        }
        Preconditions.checkNotNull(valueExpr);
        this.valueList = valueList;
        this.valueExpr = valueExpr;
    }

    @Override
    public String getValue() {
        return valueExpr.getValue();
    }

    @Override
    public String getValue(final Map<String, String> context) {
        return valueExpr.getValue(context);
    }

    @Override
    public List<Expr> children() {
        return Arrays.asList(valueExpr);
    }

    @Override
    public boolean evaluate(final Map<String, String> context) {
        String stringValue = getValue(context);
        if(stringValue == null) {
            return false;
        }
        return valueList.contains(stringValue);
    }
}
