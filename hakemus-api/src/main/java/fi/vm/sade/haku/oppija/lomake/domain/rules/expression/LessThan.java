package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

public class LessThan extends IntegerComparison {

    public LessThan(final Variable thisIsSmaller, final Value whenComparedToThis) {
        super(thisIsSmaller, whenComparedToThis);
    }

    @Override
    protected boolean evaluate(int left, int right) {
        return left < right;
    }

}
