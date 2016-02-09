package fi.vm.sade.haku.oppija.ui.common;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultivaluedMapUtilTest {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    @Test
    public void testToSingleValueMap() throws Exception {
        MultivaluedMapImpl multi = new MultivaluedMapImpl();
        multi.put(KEY, newArrayList(VALUE));
        Map<String, String> singleValueMap = MultivaluedMapUtil.toSingleValueMap(multi);
        assertTrue(singleValueMap.containsKey(KEY));
        assertEquals(VALUE, singleValueMap.get(KEY));
    }

    @Test
    public void excludeValues() {
        MultivaluedMapImpl multi = new MultivaluedMapImpl();
        multi.add("A", "1");
        multi.add("RF", "POW!");
        multi.add("client", "haku-app");
        MultivaluedMapUtil.removeKeys(multi, "RF", "client");
    }
}
