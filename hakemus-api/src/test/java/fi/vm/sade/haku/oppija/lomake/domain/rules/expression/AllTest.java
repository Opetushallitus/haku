package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AllTest {

    @Test(expected = NullPointerException.class)
    public void testEvaluateSomeNull() {
        new All(Value.TRUE, null, Value.FALSE);
    }

    @Test(expected = NullPointerException.class)
    public void testEvaluateAllNull() {
        new All(null, null, null, null, null);
    }

    @Test
    public void testEvaluateToFalse() {
        All all = new All(Value.TRUE, Value.FALSE);
        assertFalse(all.evaluate(null));
    }

    @Test
    public void testEvaluateTrueTrue() {
        All all = new All(Value.TRUE, Value.TRUE, Value.TRUE);
        assertTrue(all.evaluate(null));
    }

    @Test
    public void testEvaluateEmpty() {
        All all = new All();
        assertTrue(all.evaluate(null));
    }

    @Test
    public void testEvaluateFalseTrue() {
        All all = new All(Value.FALSE, Value.TRUE, Value.FALSE);
        assertFalse(all.evaluate(null));
    }

    @Test
    public void testEvaluateFalseFalse() {
        And and = new And(Value.FALSE, Value.FALSE);
        assertFalse(and.evaluate(null));
    }
}
