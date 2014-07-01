package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AndTest {

    @Test(expected = NullPointerException.class)
    public void testEvaluateLeftNull() {
        new And(null, Value.FALSE);
    }

    @Test(expected = NullPointerException.class)
    public void testEvaluateRightNull() {
        new And(Value.FALSE, null);
    }

    @Test
    public void testEvaluateTrueFalse() {
        And and = new And(Value.TRUE, Value.FALSE);
        assertFalse(and.evaluate(null));
    }

    @Test
    public void testEvaluateTrueTrue() {
        And and = new And(Value.TRUE, Value.TRUE);
        assertTrue(and.evaluate(null));
    }

    @Test
    public void testEvaluateFalseTrue() {
        And and = new And(Value.FALSE, Value.TRUE);
        assertFalse(and.evaluate(null));
    }

    @Test
    public void testEvaluateFalseFalse() {
        And and = new And(Value.FALSE, Value.FALSE);
        assertFalse(and.evaluate(null));
    }
}
