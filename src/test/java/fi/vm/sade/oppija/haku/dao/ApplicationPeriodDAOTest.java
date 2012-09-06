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
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationPeriodDAOTest {

    @Autowired
    TestDBFactoryBean dbFactory;

    @Autowired
    private ApplicationPeriodDAO applicationPeriodDAO;
    private final TestDataCreator testDataCreator = new TestDataCreator();

    private static DBObject testDataObject;

    @BeforeClass
    public static void readTestData() {
        StringBuffer buffer = new StringBuffer();

        try {
            Resource testDataResource = new ClassPathResource("test-data.json");

            BufferedReader reader = new BufferedReader(new FileReader(testDataResource.getFile()));

            String newLine = reader.readLine();

            while (newLine != null) {

                buffer.append(newLine);
                newLine = reader.readLine();
            }

            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        testDataObject = (DBObject) JSON.parse(buffer.toString());
    }

    @Before
    public void insertTestData() {

        try {
            dbFactory.getObject().getCollection("haku").insert(testDataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @After
    public void removeTestData() {
        try {
            dbFactory.getObject().getCollection("haku").remove(new BasicDBObject());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testInsertApplicationPeriod() {
        Map<String, Object> applicationPeriod = testDataCreator.createApplicationPeriod();

        applicationPeriodDAO.insert(applicationPeriod);
    }

    @Test
    public void testFindForm() {
        Map<String, Object> form = applicationPeriodDAO.findForm("yhteishaku", "yhteishakulomake-id");


        assertNotNull(form);
    }

}
