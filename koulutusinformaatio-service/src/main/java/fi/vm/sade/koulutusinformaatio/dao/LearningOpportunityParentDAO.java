package fi.vm.sade.koulutusinformaatio.dao;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;
import fi.vm.sade.koulutusinformaatio.dao.entity.LearningOpportunityParentEntity;

/**
 * @author Mikko Majapuro
 */
public class LearningOpportunityParentDAO extends BasicDAO<LearningOpportunityParentEntity, String> {

    public LearningOpportunityParentDAO(Mongo mongo, Morphia morphia, String dbName) {
        super(mongo, morphia, dbName);
    }
}
