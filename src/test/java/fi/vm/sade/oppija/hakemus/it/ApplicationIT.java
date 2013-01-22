package fi.vm.sade.oppija.hakemus.it;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.common.it.AbstractRemoteTest;
import fi.vm.sade.oppija.hakemus.domain.Application;
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
            dbFactory.getObject().getCollection("application").insert(applicationTestDataObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindByApplicationOption() throws IOException {
        beginAt("applications?aoid=776");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        List<Application> applications = mapper.readValue(response, new TypeReference<List<Application>>() { });
        assertEquals(2, applications.size());
    }

    @Test
    public void testFindByInvalidApplicationOption() throws IOException {
        beginAt("applications?aoid=INVALID");
        String response = getPageSource();

        ObjectMapper mapper = new ObjectMapper();
        List<Application> applications = mapper.readValue(response, new TypeReference<List<Application>>() { });
        assertEquals(0, applications.size());
    }

}
