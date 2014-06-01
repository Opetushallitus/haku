package fi.vm.sade.haku.oppija.repository;

import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ApplicationSystemConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApplicationSystemConfigurationRepository {

    private final MongoOperations mongoOperations;

    @Autowired
    public ApplicationSystemConfigurationRepository(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public ApplicationSystemConfiguration save(final ApplicationSystemConfiguration applicationSystemConfiguration) {
        mongoOperations.save(applicationSystemConfiguration);
        return applicationSystemConfiguration;
    }

    public ApplicationSystemConfiguration findById(final String asid) {
        return mongoOperations.findById(asid, ApplicationSystemConfiguration.class);
    }

    public List<ApplicationSystemConfiguration> list() {
        return mongoOperations.findAll(ApplicationSystemConfiguration.class);
    }
}
