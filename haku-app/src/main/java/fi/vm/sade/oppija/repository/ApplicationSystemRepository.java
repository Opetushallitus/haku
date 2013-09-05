package fi.vm.sade.oppija.repository;

import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApplicationSystemRepository {

    private final MongoOperations mongoOperations;

    @Autowired
    public ApplicationSystemRepository(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public void save(final ApplicationSystem applicationSystem) {
        mongoOperations.insert(applicationSystem);
    }

    public ApplicationSystem findById(final String asid) {
        return mongoOperations.findById(asid, ApplicationSystem.class);
    }

    public List<ApplicationSystem> findAll() {
        return mongoOperations.findAll(ApplicationSystem.class);
    }
}
