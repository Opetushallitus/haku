package fi.vm.sade.oppija.lomake.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilTest {
    @Test
    public void testParseOidSuffixNull() {
        assertEquals(null, StringUtil.parseOidSuffix(null));
    }

    @Test
    public void testParseOidSuffix() {
        assertEquals("3", StringUtil.parseOidSuffix("1.2.3"));
    }

    @Test
    public void testParseOidSuffixNoDots() {
        assertEquals("1", StringUtil.parseOidSuffix("1"));
    }

    @Test
    public void testParseOidSuffixJustDot() {
        assertEquals(".", StringUtil.parseOidSuffix("."));
    }
}
