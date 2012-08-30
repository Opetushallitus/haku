package fi.vm.sade.oppija.haku;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author jukka
 * @version 8/30/125:01 PM}
 * @since 1.1
 */
public class HelloTest {
    @Test
    public void testDoHello() throws Exception {
        assertEquals("world", new Hello().doHello());
    }
}
