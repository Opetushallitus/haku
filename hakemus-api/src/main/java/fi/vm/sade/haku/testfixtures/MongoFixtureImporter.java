package fi.vm.sade.haku.testfixtures;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;

public class MongoFixtureImporter {
    public static void importJsonFixtures(MongoTemplate template, ApplicationDAO dao) throws IOException {
        final Resource[] resources = new PathMatchingResourcePatternResolver().getResources("mongofixtures/**/*.json");
        for (Resource resource: resources) {
            // do twice to ensure idempotence
            insertObject(template, resource);
            insertObject(template, resource);
        }
        FixtureSSNFixer.updateEmptySsnInApplications(TestFixtureConstants.personOid, TestFixtureConstants.hetu, dao);
    }

    private static void insertObject(final MongoTemplate template, final Resource resource) throws IOException {
        String collection = getParentName(resource);
        final String jsonString = IOUtils.toString(resource.getURI());
        try {
            final DBObject dbObject = (DBObject) JSON.parse(jsonString);
            // TODO: doesn't seem to work with multiple objects per file (themequestion/*.json)
            upsert(template, collection, dbObject);
        } catch (Exception e) {
            System.err.println("Dumping JSON:");
            System.err.println(jsonString);
            throw new RuntimeException("Error importing JSON from " + resource.getURL(), e);
        }
    }

    private static void upsert(final MongoTemplate template, final String collection, final DBObject dbObject) {
        final Object id = dbObject.get("_id");
        template.getCollection(collection).update(new BasicDBObject("_id", id), dbObject, true, false);
    }

    private static String getParentName(Resource resource) throws IOException {
        final String[] components = resource.getURI().toString().split("/");
        return components[components.length - 2];
    }
}
