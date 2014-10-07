package fi.vm.sade.haku.testfixtures;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;

public class MongoFixtureImporter {
    private static final Logger LOGGER = Logger.getLogger(MongoFixtureImporter.class);

    public static void importJsonFixtures(MongoTemplate template, ApplicationDAO dao) throws IOException {
        importJsonFixtures(template, dao, "**/*.json");
    }
    public static void importJsonFixtures(MongoTemplate template, ApplicationDAO dao, String selector) throws IOException {
        final Resource[] resources = new PathMatchingResourcePatternResolver().getResources("mongofixtures/" + selector);
        for (Resource resource: resources) {
            insertObject(template, resource);
        }
        FixtureSSNFixer.updateEmptySsnInApplications(TestFixtureConstants.personOid, TestFixtureConstants.hetu, dao);
    }

    public static void clearFixtures(MongoTemplate template, ApplicationDAO dao, String collection) throws IOException {
        template.getCollection(collection).remove(new BasicDBObject());
    }

    private static void insertObject(final MongoTemplate template, final Resource resource) throws IOException {
        String collection = getParentName(resource);
        final String jsonString = IOUtils.toString(resource.getURI());
        try {
            LOGGER.info("Importing " + resource.getURI() + " to collection " + collection);
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
