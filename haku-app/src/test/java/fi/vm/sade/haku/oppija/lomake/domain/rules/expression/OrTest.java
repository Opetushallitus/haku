package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrTest {

    private Value aTrue;
    private Value aFalse;

    @Before
    public void setUp() throws Exception {
        aTrue = new Value(Boolean.TRUE.toString());
        aFalse = new Value(Boolean.FALSE.toString());

    }

    @Test
    public void testEvaluateTrueFalse() {
        Or or = new Or(aTrue, aFalse);
        assertTrue(or.evaluate(null));
    }

    @Test
    public void testEvaluateFalseTrue() {
        Or or = new Or(aFalse, aTrue);
        assertTrue(or.evaluate(null));
    }

    @Test
    public void testEvaluateTrueTrue() {
        Or or = new Or(new Value("true"), aTrue);
        assertTrue(or.evaluate(null));
    }

    @Test
    public void testEvaluateToFalseFalse() {
        Or or = new Or(aFalse, aFalse);
        assertFalse(or.evaluate(Collections.<String, String>emptyMap()));
    }

    @Test(expected = NullPointerException.class)
    public void testCreateLeftNull() {
        new Or(null, aFalse);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateRightNull() {
        new Or(aTrue, null);
    }
}
