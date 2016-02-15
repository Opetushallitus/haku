package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OlderThanTest {

    public static final String DATE_OF_BIRTH_KEY = "syntymaaika";
    private OlderThan olderThan;

    @Before
    public void setUp() throws Exception {
        this.olderThan = new OlderThan(new Value("16"));
    }

    @Test
    public void testEvaluate() throws Exception {
        assertTrue(olderThan.evaluate(ImmutableMap.of(DATE_OF_BIRTH_KEY, "12.2.1980")));
    }

    @Test
    public void testEvaluateYounger() throws Exception {
        assertFalse(olderThan.evaluate(ImmutableMap.of(DATE_OF_BIRTH_KEY, "12.2.2005")));
    }

    @Test
    public void testEvaluateAnswer() throws Exception {
        assertFalse(olderThan.evaluate(new HashMap<String, String>()));
    }
}
