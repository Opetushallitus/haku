package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.base.Preconditions;

import java.util.Map;

import static com.google.common.base.Strings.nullToEmpty;

public class Regexp extends Variable {
    private final String pattern;

    public Regexp(final String value, final String pattern) {
        super(value);
        Preconditions.checkNotNull("Regexp pattern can not be null", pattern);
        this.pattern = pattern;
    }

    @Override
    public boolean evaluate(Map<String, String> context) {
        return nullToEmpty(getValue(context)).matches(pattern);
    }
}

