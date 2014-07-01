package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VariableTest {

    public static final String VARIABLE_NAME = "id";
    public static final String CONTEXT_VALUE = "1";
    public static final String EMPTY_STRING = "";

    @Test
    public void testGetValue() {
        assertEquals(CONTEXT_VALUE,
                new Variable(VARIABLE_NAME).getValue(ImmutableMap.of(VARIABLE_NAME, CONTEXT_VALUE)));
    }

    @Test
    public void testGetValueNotFound() {
        assertEquals(null,
                new Variable(VARIABLE_NAME).getValue(ImmutableMap.of(VARIABLE_NAME + "zztop", CONTEXT_VALUE)));
    }

    @Test
    public void testGetValueEmpty() {
        assertEquals(EMPTY_STRING,
                new Variable(VARIABLE_NAME).getValue(ImmutableMap.of(VARIABLE_NAME, EMPTY_STRING)));
    }
}
