package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ValueListContains extends Expr {

    private final List<String> valueList;
    private final Expr value;

    public ValueListContains(final List<String> valueList, final Expr value) {
        Preconditions.checkNotNull(valueList);
        for(String item: valueList) {
            Preconditions.checkNotNull(item);
        }
        Preconditions.checkNotNull(value);
        this.valueList = valueList;
        this.value = value;
    }

    @Override
    public String getValue() {
        return value.getValue();
    }

    @Override
    public String getValue(final Map<String, String> context) {
        return value.getValue(context);
    }

    @Override
    public List<Expr> children() {
        return Collections.EMPTY_LIST;
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
