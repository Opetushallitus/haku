package fi.vm.sade.haku.oppija.repository;

import fi.vm.sade.log.model.Tapahtuma;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogRepository {

    private static final Logger log = LoggerFactory.getLogger(AuditLogRepository.class);

    private final MongoOperations mongoOperations;

    private static final String collectionName = "auditlog";

    @Autowired
    public AuditLogRepository(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public void save(final Tapahtuma tapahtuma) {
        try {
            mongoOperations.save(tapahtuma, collectionName);
        } catch (RuntimeException e) {
            log.error("Failed to save auditlog for {}", new TapahtumaToStringWrapper(tapahtuma), e);
        }
    }


    public class TapahtumaToStringWrapper{

        private final Tapahtuma tapahtuma;
        public TapahtumaToStringWrapper(final Tapahtuma tapahtuma){
            this.tapahtuma = tapahtuma;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(tapahtuma);
        }
    }
}
