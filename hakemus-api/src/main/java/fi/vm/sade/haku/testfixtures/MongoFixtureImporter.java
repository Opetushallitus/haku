package fi.vm.sade.haku.testfixtures;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class MongoFixtureImporter {
    public static void importJsonFixtures(MongoTemplate template) throws IOException {
        final Resource[] resources = new PathMatchingResourcePatternResolver().getResources("mongofixtures/**/*.json");
        for (Resource resource: resources) {
            String collection = getParentName(resource);
            final String jsonString = IOUtils.toString(resource.getURI());
            try {
                final DBObject dbObject = (DBObject) JSON.parse(jsonString);
                template.getCollection(collection).insert(dbObject);
            } catch (Exception e) {
                System.err.println("Dumping JSON:");
                System.err.println(jsonString);
                throw new RuntimeException("Error importing JSON from " + resource.getURL(), e);
            }
        }
    }

    private static String getParentName(Resource resource) throws IOException {
        final String[] components = resource.getURI().getPath().split("/");
        return components[components.length - 2];
    }
}
