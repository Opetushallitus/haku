package fi.vm.sade.oppija.haku.dao;

import fi.vm.sade.oppija.haku.domain.Hakemus;
import fi.vm.sade.oppija.haku.domain.HakemusId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * @author Hannu Lyytikainen
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationDAOTest extends AbstractDAOTest {

    @Autowired
    @Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    @Test
    public void testUpdateAndFindApplication() {
        String id = System.currentTimeMillis() + "";
        final HakemusId id1 = new HakemusId(id, id, id, id);
        final HashMap<String, String> values = new HashMap<String, String>();
        values.put("avain", "arvo");
        applicationDAO.update(new Hakemus(id1, values));

        HakemusId id2 = new HakemusId(id, id, id, id);
        final Hakemus hakemus = applicationDAO.find(id2);
        assertEquals(hakemus.getValues().get("avain"), "arvo");
    }

    @Override
    protected String getCollectionName() {
        return "hakemus";
    }

}
