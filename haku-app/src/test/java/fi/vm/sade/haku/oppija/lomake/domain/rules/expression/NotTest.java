package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NotTest {
    @Test
    public void testEvaluateTrue() {
        Not aFalse = new Not(Value.TRUE);
        assertFalse(aFalse.evaluate(Collections.<String, String>emptyMap()));
    }

    @Test
    public void testEvaluateFalse() {
        Not aFalse = new Not(Value.FALSE);
        assertTrue(aFalse.evaluate(Collections.<String, String>emptyMap()));
    }

    @Test
    public void testEvaluateVariableFalse() {
        Not notId = new Not(new Variable("id"));
        assertFalse(notId.evaluate(ImmutableMap.of("id", Boolean.TRUE.toString())));
    }

    @Test
    public void testEvaluateVariableTrue() {
        Not aTrue = new Not(new Variable("id"));
        assertTrue(aTrue.evaluate(ImmutableMap.of("id", Boolean.FALSE.toString())));
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        new Not(null);
    }
}
