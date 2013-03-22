package fi.vm.sade.koulutusinformaatio.dao;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;
import fi.vm.sade.koulutusinformaatio.dao.entity.ApplicationOptionEntity;

/**
 * @author Mikko Majapuro
 */
public class ApplicationOptionDAO extends BasicDAO<ApplicationOptionEntity, String> {

    public ApplicationOptionDAO(Mongo mongo, Morphia morphia, String dbName) {
        super(mongo, morphia, dbName);
    }
}
