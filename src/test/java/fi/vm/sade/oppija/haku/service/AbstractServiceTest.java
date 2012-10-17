package fi.vm.sade.oppija.haku.service;

import com.mongodb.BasicDBObject;
import fi.vm.sade.oppija.haku.dao.TestDBFactoryBean;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Abstract base class for service tests. Provides test data removal from multiple
 * collections between tests.
 *
 * @author Hannu Lyytikainen
 */
public abstract class AbstractServiceTest {

    @Autowired
    TestDBFactoryBean dbFactory;

    @After
    public void removeTestData() {
        try {
            for (String collectionName : getCollectionNames()) {
                dbFactory.getObject().getCollection(collectionName).remove(new BasicDBObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract List<String> getCollectionNames();

}
