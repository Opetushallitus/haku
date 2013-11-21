package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import org.junit.Test;

import java.util.Collections;

public class EqualsTest {

    @Test(expected = NullPointerException.class)
    public void testCreateLeftNull() {
        new Equals(null, new Value(""));
    }

    @Test(expected = NullPointerException.class)
    public void testCreateRightNull() {
        new Equals(new Value(""), null);
    }

    @Test
    public void testTrueNullValues() {
        Equals equals = new Equals(new Variable("1d1"), new Variable("i2"));
        equals.evaluate(Collections.<String, String>emptyMap());
    }

    @Test
    public void testTrue() {
        Equals equals = new Equals(Value.TRUE, Value.TRUE);
        equals.evaluate(Collections.<String, String>emptyMap());
    }

    @Test
    public void testFalse() {
        Equals equals = new Equals(Value.TRUE, Value.FALSE);
        equals.evaluate(Collections.<String, String>emptyMap());
    }
}
