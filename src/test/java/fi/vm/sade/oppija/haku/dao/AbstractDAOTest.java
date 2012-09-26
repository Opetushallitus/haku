package fi.vm.sade.oppija.haku.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import fi.vm.sade.oppija.haku.tools.FileHandling;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.ClassLoader.getSystemResourceAsStream;

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

    @After
    public void removeTestData() {
        try {
            dbFactory.getObject().getCollection(getCollectionName()).remove(new BasicDBObject());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract String getCollectionName();
}
