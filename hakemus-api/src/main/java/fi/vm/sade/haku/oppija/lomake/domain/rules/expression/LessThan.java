package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

public class LessThan extends IntegerComparison {

    public LessThan(final Variable left, final Value right) {
        super(left, right);
    }

    @Override
    protected boolean evaluate(int left, int right) {
        return left < right;
    }

}
