package fi.vm.sade.haku.oppija.lomake.domain.rules.expression;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class BeforeTest {

    public static final String AGE = "16";

    private OlderThan olderThan = new OlderThan(new Value(AGE), false);

    private HashMap<String, String> ctx;

    @Before
    public void setUp() throws Exception {
        ctx = new HashMap<String, String>();
    }

    @Test
    public void testBeforeTrue() {
        ctx.put("syntymaaika", "15.5.1985");
        assertTrue(olderThan.evaluate(ctx));
    }

    @Test
    public void testBeforeFalse() {
        ctx.put("syntymaaika", "15.5.2005");
        assertFalse(olderThan.evaluate(ctx));
    }
}
