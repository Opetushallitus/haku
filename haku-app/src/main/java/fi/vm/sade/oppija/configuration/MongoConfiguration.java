package fi.vm.sade.oppija.configuration;

import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class MongoConfiguration {

    @Bean
    public MongoOperations mongoTemplate(final Mongo mongo, @Value("${mongo.db.name}") final String collection) {
        return new MongoTemplate(mongo, collection);
    }

    @Bean
    public MongoFactoryBean mongo(final @Value("${mongodb.url}") String mongoUrl) throws URISyntaxException {
        MongoFactoryBean mongo = new MongoFactoryBean();
        URI url1 = new URI(mongoUrl);
        mongo.setHost(url1.getHost());
        int port = url1.getPort();
        if (port > 0) {
            mongo.setPort(port);
        }
        return mongo;
    }
}
