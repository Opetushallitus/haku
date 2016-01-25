package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AllTest {

    @Test(expected = NullPointerException.class)
    public void testEvaluateSomeNull() {
        new All(Arrays.asList(Value.TRUE, null, Value.FALSE));
    }

    @Test
    public void testEvaluateToFalse() {
        All all = new All(Arrays.asList(Value.TRUE, Value.FALSE));
        assertFalse(all.evaluate(null));
    }

    @Test
    public void testEvaluateTrueTrue() {
        All all = new All(Arrays.asList(Value.TRUE, Value.TRUE, Value.TRUE));
        assertTrue(all.evaluate(null));
    }

    @Test
    public void testEvaluateEmpty() {
        All all = new All();
        assertTrue(all.evaluate(null));
    }

    @Test
    public void testEvaluateFalseTrue() {
        All all = new All(Arrays.asList(Value.FALSE, Value.TRUE, Value.FALSE));
        assertFalse(all.evaluate(null));
    }

    @Test
    public void testEvaluateFalseFalse() {
        All all = new All(Arrays.asList(Value.FALSE, Value.FALSE));
        assertFalse(all.evaluate(null));
    }

    @Test
    public void testNestedAllExpression() {
        All all = new All(Arrays.asList(
                new All(Arrays.asList(Value.TRUE, Value.TRUE)),
                new All(),
                new All(Collections.singletonList(Value.TRUE))));
        assertTrue(all.evaluate(null));
    }
}
