package fi.vm.sade.oppija.haku.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationPeriodDAOTest extends AbstractDAOTest {

    @Autowired
    private ApplicationPeriodDAO applicationPeriodDAO;
    private final TestDataCreator testDataCreator = new TestDataCreator();

    @Test
    public void testInsertApplicationPeriod() {
        Map<String, Object> applicationPeriod = testDataCreator.createApplicationPeriod();
        applicationPeriodDAO.insert(applicationPeriod);
    }

    @Test
    public void testFindAll() {
        List applicationPeriods = applicationPeriodDAO.findAll();
        assertNotNull(applicationPeriods);
        assertEquals("Found insufficient number of applicationPeriods", 1, applicationPeriods.size());
    }

    @Test
    public void testFindForm() {
        Map<String, Object> form = applicationPeriodDAO.findForm("yhteishaku", "yhteishakulomake-id");
        assertNotNull(form);
    }

}
