package fi.vm.sade.oppija.haku.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
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
}
