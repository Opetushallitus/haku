package fi.vm.sade.koulutusinformaatio.dao;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;
import fi.vm.sade.koulutusinformaatio.dao.entity.ParentLearningOpportunityEntity;

/**
 * @author Mikko Majapuro
 */
public class ParentLearningOpportunityDAO extends BasicDAO<ParentLearningOpportunityEntity, String> {

    public ParentLearningOpportunityDAO(Mongo mongo, Morphia morphia, String dbName) {
        super(mongo, morphia, dbName);
    }
}
