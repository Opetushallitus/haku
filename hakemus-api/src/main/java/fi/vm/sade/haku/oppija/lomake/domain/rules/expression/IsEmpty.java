package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IsEmpty extends Expr {
    private final Expr left;
    public IsEmpty(Expr left) {
        this.left = left;
        Preconditions.checkNotNull(left);
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        return StringUtils.isEmpty(left.getValue());
    }

    @Override
    public List<Expr> children() {
        return Arrays.asList(left);
    }
}
