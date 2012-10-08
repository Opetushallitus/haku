package fi.vm.sade.oppija.tarjonta.converter;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author jukka
 * @version 10/8/1212:38 PM}
 * @since 1.1
 */
public class ArrayParametersToMapTest {

    @Test
    public void test() throws Exception {
        final Map<String, String> convert = new ArrayParametersToMap().convert(new String[]{"foo", "bar"});
        assertEquals("foo", convert.get("foo"));
    }
}
