package fi.vm.sade.haku.oppija.repository;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
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
        log.debug("Find all ApplicationSystems (include fields: {})", Arrays.toString(includeFields));
        Query q = new Query();
        for (String includeField : includeFields) {
            q.fields().include(includeField);

        }
        List<ApplicationSystem> applicationSystems = mongoOperations.find(q, ApplicationSystem.class);
        log.debug("Found {} applicationSystems", applicationSystems.size());
        return applicationSystems;
    }

    public List<ApplicationSystem> findBySemesterAndYear(String semester, String year) {
        Query q = new Query();
        if (isNotEmpty(semester)) {
            q.addCriteria(new Criteria("hakukausiUri").is(semester));
        }
        if (isNotEmpty(year)) {
            q.addCriteria(new Criteria("hakukausiVuosi").is(Integer.valueOf(year)));
        }
        log.debug("findBySemesterAndYear({}, {}) query: {}", semester, year, q.toString());
        q.toString();
        return mongoOperations.find(q, ApplicationSystem.class);
    }
}
