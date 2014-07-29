package fi.vm.sade.haku.oppija.configuration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;

@Configuration
public class MongoConfiguration {

    @Bean
    public MongoTemplate mongoTemplate(final Mongo mongo, @Value("${mongo.db.name}") final String databaseName,
                                       @Value("${mongo.writeconcern") final String writeConcern) {
        MongoTemplate tmpl = new MongoTemplate(mongo, databaseName);
        WriteConcern wc = resolveWriteConcern(writeConcern);
        if (wc != null) {
            tmpl.setWriteConcern(wc);
        }
        return tmpl;
    }

    @Bean
    public MongoClient mongo(@Value("${mongodb.url}") final String mongoUri,
                             @Value("${mongo.writeconcern") final String writeConcern) throws UnknownHostException {
        MongoClientURI mongoClientURI = new MongoClientURI(mongoUri);
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        WriteConcern wc = resolveWriteConcern(writeConcern);
        if (wc != null) {
            mongoClient.setWriteConcern(wc);
        }
        return mongoClient;
    }

    private WriteConcern resolveWriteConcern(String writeConcern) {
        if ("ACKNOWLEDGED".equals(writeConcern)) {
            return WriteConcern.ACKNOWLEDGED;
        } else if ("MAJORITY".equals(writeConcern)) {
            return WriteConcern.MAJORITY;
        }

        return null;
    }


}
