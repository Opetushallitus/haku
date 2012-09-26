package fi.vm.sade.oppija.haku.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDBFactoryBean extends DBFactoryBean {

    private static final Logger logger = LoggerFactory.getLogger(TestDBFactoryBean.class);

    @Override
    public void shutDown() {
        try {
            getObject().dropDatabase();
        } catch (Exception e) {
            logger.warn("Could not drop test database.");
        }

        mongo.close();
    }

}
