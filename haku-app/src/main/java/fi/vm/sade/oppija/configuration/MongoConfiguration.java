package fi.vm.sade.oppija.configuration;

import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfiguration {


    public
    @Bean
    MongoOperations mongoTemplate(final Mongo mongo, final @Value("${mongo.db.name}") String collection) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongo, collection);
        return mongoTemplate;
    }

    /*
     * Factory bean that creates the Mongo instance
     */
    public
    @Bean
    MongoFactoryBean mongo() {
        MongoFactoryBean mongo = new MongoFactoryBean();
        mongo.setHost("localhost");
        mongo.setWriteConcern(WriteConcern.FSYNC_SAFE);
        return mongo;
    }
}
