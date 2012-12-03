package fi.vm.sade.oppija.haku.domain.rules;

import fi.vm.sade.oppija.lomake.domain.rules.RegexRule;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * @author jukka
 * @version 10/3/123:34 PM}
 * @since 1.1
 */
public class RegexRuleTest {
    @Test
    public void testEvaluate() throws Exception {
        // tarkistetaan onko mies
        assertTrue(RegexRule.evaluate("010188-123X", "\\d{6}\\S\\d{2}[13579]\\w"));
    }
}
