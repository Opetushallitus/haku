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
    @Autowired
    SampleDAO sampleDAO;

    @Test
    public void testInsertAndList() throws Exception {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("eka", "value");
        sampleDAO.insert(map);
        final Map result = sampleDAO.listAll();
        assertEquals(result.get("eka"), map.get("eka"));
    }


}
