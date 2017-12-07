package fi.vm.sade.haku.oppija.ui.common;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.Map;

public class MultivaluedMapUtilTest {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    @Test
    public void testToSingleValueMap() {
        MultivaluedHashMap<String,String> multi = new MultivaluedHashMap<>();
        multi.put(KEY, newArrayList(VALUE));
        Map<String, String> singleValueMap = MultivaluedMapUtil.toSingleValueMap(multi);
        assertTrue(singleValueMap.containsKey(KEY));
        assertEquals(VALUE, singleValueMap.get(KEY));
    }

    @Test
    public void excludeValues() {
        MultivaluedHashMap<String,String> multi = new MultivaluedHashMap<>();
        multi.add("A", "1");
        multi.add("RF", "POW!");
        multi.add("client", "haku-app");
        MultivaluedMapUtil.removeKeys(multi, "RF", "client");
    }
}
