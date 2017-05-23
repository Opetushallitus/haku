package fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl;

import fi.vm.sade.haku.oppija.common.dao.AbstractDAOMongoImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.FormConfigurationDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.DBObjectToFormConfigurationFunction;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.impl.DBConverter.FormConfigurationToDBObjectFunction;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service("formConfigurationDAOMongoImpl")
public class FormConfigurationDAOMongoImpl extends AbstractDAOMongoImpl<FormConfiguration> implements FormConfigurationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormConfigurationDAOMongoImpl.class);


    private static final String INDEX_APPLICATION_SYSTEM_ID = "index_applicationSystemId";
    private static final String FIELD_APPLICATION_SYSTEM_ID = "applicationSystemId";

    @Value("${mongodb.ensureIndex:true}")
    private boolean ensureIndex;

    @Autowired
    public FormConfigurationDAOMongoImpl(DBObjectToFormConfigurationFunction dbObjectToFormConfigurationConverter,
      FormConfigurationToDBObjectFunction formConfigurationToBasicDBObjectConverter) {
        super("formconfiguration", dbObjectToFormConfigurationConverter, formConfigurationToBasicDBObjectConverter);
    }

    @PostConstruct
    public void configure() {
        if (!ensureIndex) {
            return;
        }
        checkIndexes("before ensures");
        // constraint indexes
        ensureUniqueIndex(INDEX_APPLICATION_SYSTEM_ID, FIELD_APPLICATION_SYSTEM_ID);

        //other ?
        checkIndexes("after ensures");
    }

    @Override
    public FormConfiguration findByApplicationSystem(String asId) {
        final FormConfiguration searchConfiguration = new FormConfiguration(asId);
        LOGGER.debug("findById: " + asId);
        List<FormConfiguration> formConfigurations = find(searchConfiguration);
        if (formConfigurations.size() == 1)
            return formConfigurations.get(0);
        return null;
    }

    @Override
    public void update(FormConfiguration formConfiguration) {
        FormConfiguration searchConfiguration = new FormConfiguration(formConfiguration.getApplicationSystemId());
        super.update(searchConfiguration, formConfiguration);
    }
}
