package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegexpTest {

    public static final String PATTERN = "testaaja";
    public static final String QUESTION = "nimi";
    private Regexp regexp;

    @Before
    public void setUp() throws Exception {
        regexp = new Regexp(QUESTION, PATTERN);
    }

    @Test
    public void testEvaluateTrue() throws Exception {
        assertTrue(regexp.evaluate(ImmutableMap.of(QUESTION, PATTERN)));
    }

    @Test
    public void testEvaluateFalse() throws Exception {
        assertFalse(regexp.evaluate(ImmutableMap.of(QUESTION, PATTERN + "h")));
    }

    @Test
    public void testEvaluateEmpty() throws Exception {
        assertFalse(regexp.evaluate(new HashMap<String, String>(0)));
    }
}
