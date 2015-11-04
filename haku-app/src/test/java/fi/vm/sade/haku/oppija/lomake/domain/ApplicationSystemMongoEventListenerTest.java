package fi.vm.sade.haku.oppija.lomake.domain;

import com.google.common.cache.LoadingCache;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fi.vm.sade.haku.oppija.common.mongo.DBObjectUtils;
import fi.vm.sade.hakutest.IntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static fi.vm.sade.haku.oppija.common.mongo.DBObjectUtils.decompressDBObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApplicationSystemMongoEventListenerTest extends IntegrationTest {
    private static final String AFFECTED_APPLICATION_SYSTEM_ID = "1.2.246.562.29.95390561488";

    @Before
    public void setup() {
        mongoServer.dropCollections();

        // Save compressed application and clear cache
        applicationSystemService.save(formGenerator.generate(AFFECTED_APPLICATION_SYSTEM_ID));
        LoadingCache<String, ApplicationSystem> cache = applicationSystemService.getCache();
        assertEquals(1, cache.size());
        cache.invalidateAll();
        assertEquals(0, cache.size());
    }

    @Test
    public void applicationSystemFormIsPersistedInCompressedForm() {
        DBObject result = mongoTemplate.getDb().getCollection("applicationSystem").findOne(AFFECTED_APPLICATION_SYSTEM_ID);
        assertTrue(DBObjectUtils.isGZIP((byte[]) result.get("form")));
        assertApplicationSystem(applicationSystemService.getApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID));
    }

    @Test
    public void applicationSystemFormDecompressionIsBackwardsCompatibleIsPersistedInCompressedForm() throws IOException, ClassNotFoundException {
        DBCollection collection = mongoTemplate.getDb().getCollection("applicationSystem");
        DBObject result = collection.findOne(AFFECTED_APPLICATION_SYSTEM_ID);
        result.put("form", decompressDBObject((byte[]) result.get("form")));
        collection.save(result);

        DBObject decompressedResult = collection.findOne(AFFECTED_APPLICATION_SYSTEM_ID);
        assertEquals(AFFECTED_APPLICATION_SYSTEM_ID, ((Map)decompressedResult.get("form")).get("_id"));
        assertApplicationSystem(applicationSystemService.getApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID));
    }

    private void assertApplicationSystem(ApplicationSystem readFromDatabase) {
        assertEquals(1, applicationSystemService.getCache().size());
        assertEquals(5, readFromDatabase.getForm().getChildren().size());
        assertEquals(AFFECTED_APPLICATION_SYSTEM_ID, readFromDatabase.getForm().getId());
        assertEquals("lukion-paattotodistuksen-keskiarvo", readFromDatabase.getForm()
                .getChildren().get(3).getChildren().get(0)
                .getChildren().get(0).getChildren().get(0).getId());
    }
}
