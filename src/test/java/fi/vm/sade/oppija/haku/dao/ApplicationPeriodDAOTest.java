package fi.vm.sade.oppija.haku.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationPeriodDAOTest {

    @Autowired
    private ApplicationPeriodDAO applicationPeriodDAO;
    private final TestDataCreator testDataCreator = new TestDataCreator();

    @Test
    public void testInsertApplicationPeriod() {
        Map<String, Object> applicationPeriod = testDataCreator.createApplicationPeriod();

        applicationPeriodDAO.insert(applicationPeriod);

    }

    private Map<String, Object> createForm() {

        return testDataCreator.createForm();
    }

}
