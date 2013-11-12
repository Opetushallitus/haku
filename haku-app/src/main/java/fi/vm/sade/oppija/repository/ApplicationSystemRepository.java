package fi.vm.sade.oppija.repository;

import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Repository
public class ApplicationSystemRepository {

    private static final Logger log = LoggerFactory.getLogger(ApplicationSystemRepository.class);

    private final MongoOperations mongoOperations;

    @Autowired
    public ApplicationSystemRepository(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public void save(final ApplicationSystem applicationSystem) {
        mongoOperations.save(applicationSystem);
    }

    public ApplicationSystem findById(final String asid) {
        return mongoOperations.findById(asid, ApplicationSystem.class);
    }

    public List<ApplicationSystem> findAll(String... includeFields) {
        Query q = new Query();
        for (String includeField : includeFields) {
            q.fields().include(includeField);

        }
        return mongoOperations.find(q, ApplicationSystem.class);
    }

    public List<ApplicationSystem> findBySemesterAndYear(String semester, String year) {
        Query q = new Query();
        if (isNotEmpty(semester)) {
            q.addCriteria(new Criteria("hakukausiUri").is(semester));
        }
        if (isNotEmpty(year)) {
            q.addCriteria(new Criteria("hakukausiVuosi").is(Integer.valueOf(year)));
        }
        return mongoOperations.find(q, ApplicationSystem.class);
    }
}
