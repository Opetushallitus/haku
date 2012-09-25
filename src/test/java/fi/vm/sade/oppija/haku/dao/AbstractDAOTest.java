package fi.vm.sade.oppija.haku.dao;

import com.mongodb.BasicDBList;
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

    protected static DBObject applicationPeriodTestDataObject;
    protected static DBObject applicationTestDataObject;

    @BeforeClass
    public static void readTestData() {

        StringBuilder applicationPeriodBuffer = new FileHandling().readFile(ClassLoader.getSystemResourceAsStream("test-data.json"));
        applicationPeriodTestDataObject = (DBObject) JSON.parse(applicationPeriodBuffer.toString());

        StringBuilder applicationBuffer = new FileHandling().readFile(ClassLoader.getSystemResourceAsStream("application-test-data.json"));
        applicationTestDataObject = (DBObject) JSON.parse(applicationBuffer.toString());

    }

    @Before
    public void insertTestData() {

        try {
            dbFactory.getObject().getCollection(getCollectionName()).insert(getTestDataObject());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @After
    public void removeTestData() {
        try {
            dbFactory.getObject().getCollection(getCollectionName()).remove(new BasicDBObject());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract String getCollectionName();
    protected abstract DBObject getTestDataObject();
}
