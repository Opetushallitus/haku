package fi.vm.sade.haku.oppija.configuration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoConfiguration.class);

    @Value("${mongo.db.name}")
    private String databaseName;

    @Value("${mongo.writeconcern")
    private String writeConcern;

    @Value("${mongodb.url}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public Mongo mongo() throws Exception {
        LOGGER.info("Creating MongoClient for server(s): " + sanitizeMongoUri(mongoUri));
        MongoClientURI mongoClientURI = new MongoClientURI(mongoUri);
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        WriteConcern wc = resolveWriteConcern(writeConcern);
        if (wc != null) {
            mongoClient.setWriteConcern(wc);
        }
        return mongoClient;
    }

    private static String sanitizeMongoUri(String mongoUri) {
        if (mongoUri.contains("@")) {
            return mongoUri.substring(mongoUri.indexOf("@"));
        }
        if (mongoUri.contains("//")) {
            return mongoUri.substring(mongoUri.indexOf("//"));
        }
        return mongoUri;
    }

    private static WriteConcern resolveWriteConcern(String writeConcern) {
        if ("ACKNOWLEDGED".equals(writeConcern)) {
            return WriteConcern.ACKNOWLEDGED;
        } else if ("MAJORITY".equals(writeConcern)) {
            return WriteConcern.MAJORITY;
        }
        return null;
    }
}
