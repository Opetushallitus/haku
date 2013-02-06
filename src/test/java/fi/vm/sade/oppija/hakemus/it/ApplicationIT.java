package fi.vm.sade.oppija.hakemus.it;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.common.it.AbstractRemoteTest;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.dto.ApplicationDTO;
import fi.vm.sade.oppija.lomake.dao.TestDBFactoryBean;
import fi.vm.sade.oppija.lomake.tools.FileHandling;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.getPageSource;

/**
 * @author Hannu Lyytikainen
 */
public class ApplicationIT extends AbstractRemoteTest {

    @Autowired
    TestDBFactoryBean dbFactory;

    protected static List<DBObject> applicationTestDataObject;

    @BeforeClass
    public static void readTestData() {
        String content = new FileHandling().readFile(getSystemResourceAsStream("application-test-data.json"));
        applicationTestDataObject = (List<DBObject>) JSON.parse(content);
    }

    @Before
    public void setUp() throws Exception {
        super.initTestEngine();
        try {
            dbFactory.getObject().getCollection("application").drop();
            dbFactory.getObject().getCollection("application").insert(applicationTestDataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindByApplicationOption() throws IOException {
        beginAt("applications/process/?aoid=776");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        List<ApplicationDTO> applications = mapper.readValue(response, new TypeReference<List<ApplicationDTO>>() { });
        assertEquals(2, applications.size());
    }

    @Test
    public void testFindByInvalidApplicationOption() throws IOException {
        beginAt("applications/process/?aoid=INVALID");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        List<ApplicationDTO> applications = mapper.readValue(response, new TypeReference<List<ApplicationDTO>>() { });
        assertEquals(0, applications.size());
    }


    @Test
    public void testFindAllApplications() throws IOException {
        beginAt("applications");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        List<Application> applications = mapper.readValue(response, new TypeReference<List<Application>>() { });
        assertEquals(2, applications.size());
    }

    @Test
    public void testFindApplications() throws IOException {
        beginAt("applications?q=1.2.3.4.5.3");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        List<Application> applications = mapper.readValue(response, new TypeReference<List<Application>>() { });
        assertEquals(1, applications.size());
    }

    @Test
    public void testFindApplicationsNoMatch() throws IOException {
        beginAt("applications?q=nomatch");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        List<Application> applications = mapper.readValue(response, new TypeReference<List<Application>>() { });
        assertEquals(0, applications.size());
    }

    @Test
    public void testGetApplication() throws IOException {
        beginAt("applications/1.2.3.4.5.3/");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        Application application = mapper.readValue(response, new TypeReference<Application>() { });
        assertNotNull(application);
        assertEquals("1.2.3.4.5.3", application.getOid());
    }
}
