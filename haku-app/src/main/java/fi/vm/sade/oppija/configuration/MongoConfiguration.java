package fi.vm.sade.oppija.configuration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;

@Configuration
public class MongoConfiguration {

    @Bean
    public MongoOperations mongoTemplate(final Mongo mongo, @Value("${mongo.db.name}") final String databaseName) {
        return new MongoTemplate(mongo, databaseName);
    }

    @Bean
    public MongoClient mongo(@Value("${mongodb.url}") final String mongoUri) throws UnknownHostException {
        MongoClientURI mongoClientURI = new MongoClientURI(mongoUri);
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        return mongoClient;
    }


}
