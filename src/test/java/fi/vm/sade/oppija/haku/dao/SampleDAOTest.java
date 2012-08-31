package fi.vm.sade.oppija.haku.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author jukka
 * @version 8/31/123:50 PM}
 * @since 1.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class SampleDAOTest {
    private static final String KEY = "eka";
    private static final String VALUE = "value";
    @Autowired
    SampleDAO sampleDAO;

    @Test
    public void testInsertAndList() throws Exception {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY, VALUE);
        sampleDAO.insert(map);
        final Map result = sampleDAO.listAll();
        assertEquals(result.get(KEY), map.get(KEY));
    }


}
