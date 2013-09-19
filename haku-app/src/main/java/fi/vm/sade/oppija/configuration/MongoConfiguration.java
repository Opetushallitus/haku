package fi.vm.sade.oppija.configuration;

import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfiguration {

    @Bean
    public MongoOperations mongoTemplate(final Mongo mongo, @Value("${mongo.db.name}") final String collection) {
        return new MongoTemplate(mongo, collection);
    }

    @Bean
    public MongoFactoryBean mongo(@Value("${mongodb.url}") final String mongoUrl) {
        MongoURI mongoURI = new MongoURI(mongoUrl);
        MongoFactoryBean mongo = new MongoFactoryBean();
        mongo.setMongoOptions(mongoURI.getOptions());
        return mongo;
    }


}
