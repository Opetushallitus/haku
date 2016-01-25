package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NotNullTest {
    @Test
    public void testNull() {
        NotNull nn = new NotNull(null);
        assertFalse(nn.evaluate(null));
    }

    @Test
    public void testEvaluateTrue() {
        NotNull nn = new NotNull(true);
        assertTrue(nn.evaluate(null));
    }

    @Test
    public void testEvaluateFalse() {
        NotNull nn = new NotNull(false);
        assertTrue(nn.evaluate(null));
    }
    @Test
    public void testEvaluateString() {
        NotNull nn = new NotNull("foo");
        assertTrue(nn.evaluate(null));
    }
    @Test
    public void testEvaluateEmptyString() {
        NotNull nn = new NotNull("");
        assertTrue(nn.evaluate(null));
    }
}