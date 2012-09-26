package fi.vm.sade.oppija.haku.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fi.vm.sade.oppija.haku.service.Application;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Hannu Lyytikainen
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
public class ApplicationDAOTest extends AbstractDAOTest {

    @Autowired
    @Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    protected static List<DBObject> applicationTestDataObjects = new ArrayList<DBObject>();

    @BeforeClass
    public static void readTestData() {

        ObjectMapper mapper = new ObjectMapper();
        try {
            List testObjects = mapper.readValue(getSystemResourceAsStream("application-test-data.json"), List.class);
            for (Object testObject : testObjects) {
                applicationTestDataObjects.add(new BasicDBObject((Map)testObject));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Before
    public void insertTestData() {

        try {
            dbFactory.getObject().getCollection(getCollectionName()).insert(applicationTestDataObjects);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFindApplication() {
       Application application = applicationDAO.find("randomuser", "generated_application_id");
        assertNotNull("Query returned null", application);
    }

    @Test
    public void testUpdateApplication() {
        Application application = applicationDAO.find("randomuser", "generated_application_id");
        application.getApplicationData().get("personal_info").put("firstnameid", "Joe");

        applicationDAO.update(application);

        Application newApplication = applicationDAO.find("randomuser", "generated_application_id");
        assertEquals("Could not update first name in application", "Joe", newApplication.getApplicationData().get("personal_info").get("firstnameid"));
    }

    @Test
    public void testInsertApplication() {
        Application application = createApplication();
        applicationDAO.update(application);

    }

    private Application createApplication() {

        Application application = new Application("testuser", "generted_application_id");

        Map<String, Map<String, String>> applicationData = new HashMap<String, Map<String, String>>();
        HashMap<String, String> category = new HashMap<String, String>();
        category.put("firstnameid", "Anni");
        category.put("lastname", "Alisuorittaja");
        applicationData.put("personal_info", category);
        application.setApplicationData(applicationData);

        return application;
    }

    @Override
    protected String getCollectionName() {
        return "hakemus";
    }

}
