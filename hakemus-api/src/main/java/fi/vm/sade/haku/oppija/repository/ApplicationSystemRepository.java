package fi.vm.sade.haku.oppija.repository;

import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
        log.info("Saving application system {}", applicationSystem.getId());
        try {
            mongoOperations.save(applicationSystem);
        } catch (RuntimeException e) {
            log.error("Failed to save application system " + applicationSystem.getId(), e);
            throw e;
        }
    }

    public ApplicationSystem findById(final String asid) {
        log.debug("Trying to find applicationSystem with id " + asid);
        try {
            return mongoOperations.findById(asid, ApplicationSystem.class);
        } catch (RuntimeException e) {
            log.error("Failed to find application system " + asid, e);
            throw e;
        }
    }

    public ApplicationSystem findById(final String asid, String... includeFields) {
        Criteria crit = new Criteria("_id").is(asid);
        Query q = new Query(crit);
        q.fields().include("name"); // Mandatory field
        for (String includeField : includeFields) {
            q.fields().include(includeField);
        }
        List<ApplicationSystem> result = mongoOperations.find(q, ApplicationSystem.class);
        if (result != null && result.size() == 1) {
            return result.get(0);
        }
        throw new ResourceNotFoundException("ApplicationSystem "+asid+" not found");
    }

    public List<ApplicationSystem> findAll(String... includeFields) {
        log.debug("Find all ApplicationSystems (include fields: {})", Arrays.toString(includeFields));
        Query q = new Query();
        q.fields().include("name"); // Mandatory field
        for (String includeField : includeFields) {
            q.fields().include(includeField);
        }

        List<ApplicationSystem> applicationSystems = mongoOperations.find(q, ApplicationSystem.class);
        log.debug("Found {} applicationSystems", applicationSystems.size());
        return applicationSystems;
    }

    public List<ApplicationSystem> findBySemesterAndYear(String semester, String year, String... includeFields) {
        Query q = new Query();
        if (isNotEmpty(semester)) {
            q.addCriteria(new Criteria("hakukausiUri").is(semester));
        }
        if (isNotEmpty(year)) {
            q.addCriteria(new Criteria("hakukausiVuosi").is(Integer.valueOf(year)));
        }
        q.fields().include("name"); // Mandatory field
        for (String includeField : includeFields) {
            q.fields().include(includeField);
        }
        log.debug("findBySemesterAndYear({}, {}) query: {}", semester, year, q.toString());
        return mongoOperations.find(q, ApplicationSystem.class);
    }

    public List<ApplicationSystem> findAllPublished(String[] includeFields) {
        Query q = new Query();
        q.addCriteria(new Criteria("state").is("JULKAISTU"));
        q.fields().include("name"); // Mandatory field
        for (String includeField : includeFields) {
            q.fields().include(includeField);
        }
        return mongoOperations.find(q, ApplicationSystem.class);
    }

    public int maxApplicationOptions(List<String> applicationSystemIds) {
        Query q = new Query();
        if (!applicationSystemIds.isEmpty()) {
            q.addCriteria(new Criteria("_id").in(applicationSystemIds));
        }
        q.fields().include("name"); // Mandatory field
        q.fields().include("maxApplicationOptions");
        q.with(new Sort(new Sort.Order(Sort.Direction.DESC, "maxApplicationOptions")));
        q.limit(1);
        List<ApplicationSystem> as = mongoOperations.find(q, ApplicationSystem.class);
        if (as.isEmpty()) {
            throw new ResourceNotFoundException(String.format("No application system found for oids %s", String.join(", ", applicationSystemIds)));
        }
        return as.get(0).getMaxApplicationOptions();
    }
}
