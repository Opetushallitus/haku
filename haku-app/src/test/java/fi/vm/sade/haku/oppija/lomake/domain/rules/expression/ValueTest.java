package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValueTest {

    public static final String VALUE_TXT = "1";
    private Value value;

    @Before
    public void setUp() {
        value = new Value(VALUE_TXT);

    }

    @Test(expected = NullPointerException.class)
    public void testCreateNull() {
        new Value(null);
    }

    @Test
    public void testValue() {
        assertEquals(VALUE_TXT, value.getValue(null));
    }

    @Test
    public void testEvaluateNotTrue() {
        assertFalse(value.evaluate(null));
    }

    @Test
    public void testEvaluateTrue() {
        assertTrue(new Value(Boolean.TRUE.toString()).evaluate(null));
    }

    @Test
    public void testEvaluateFalse() {
        assertFalse(new Value(Boolean.FALSE.toString()).evaluate(null));
    }
}
