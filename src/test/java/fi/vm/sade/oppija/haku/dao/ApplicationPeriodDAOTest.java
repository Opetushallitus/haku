package fi.vm.sade.oppija.haku.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationPeriodDAOTest extends AbstractDAOTest {

    @Autowired
    private ApplicationPeriodDAO applicationPeriodDAO;
    private final FormCreator formCreator = new FormCreator();

    @Test
    public void testInsertApplicationPeriod() {
        Map<String, Object> applicationPeriod = formCreator.createApplicationPeriod("yhteishaku", formCreator.createForm("haku"));
        applicationPeriodDAO.insert(applicationPeriod);
    }

    @Test
    public void testFindAll() {
        final Map<String, Map<String, Object>> applicationPeriods = applicationPeriodDAO.findAll();
        assertNotNull(applicationPeriods);
        assertEquals("Found insufficient number of applicationPeriods", 1, applicationPeriods.size());
    }

    @Test
    public void testFindForm() {
        Map<String, Object> form = applicationPeriodDAO.findForm("yhteishaku", "yhteishakulomake-id");
        assertNotNull(form);
    }

}
