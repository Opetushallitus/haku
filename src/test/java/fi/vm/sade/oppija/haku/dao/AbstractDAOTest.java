package fi.vm.sade.oppija.haku.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.haku.tools.FileHandling;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract base class for DAO tests.
 * Runs common initialization and tear down tasks like test data insertion
 * and deletion.
 *
 * @author hannu
 */
public abstract class AbstractDAOTest {

    @Autowired
    TestDBFactoryBean dbFactory;

    private static DBObject testDataObject;

    @BeforeClass
    public static void readTestData() {

        StringBuilder buffer = new FileHandling().readFile(ClassLoader.getSystemResourceAsStream("test-data.json"));

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
}
