package fi.vm.sade.haku.testfixtures;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class JsonFixtureImporter {
    public static void importJsonFixtures(MongoTemplate template) throws IOException {
        File fixturesRoot = new File(ProjectRootFinder.findProjectRoot() + "/" + "testfixtures");
        for (File collectionDirectory : fixturesRoot.listFiles()) {
            if (collectionDirectory.isDirectory()) {
                String collection = collectionDirectory.getName();
                for (File jsonFile: collectionDirectory.listFiles()) {
                    final String jsonString = IOUtils.toString(jsonFile.toURI());
                    try {
                        final DBObject dbObject = (DBObject) JSON.parse(jsonString);
                        template.getCollection(collection).insert(dbObject);
                    } catch (Exception e) {
                        System.err.println("Dumping JSON:");
                        System.err.println(jsonString);
                        throw new RuntimeException("Error importing JSON from " + jsonFile.getPath(), e);
                    }
                }
            }
        }
    }
}
