package fi.vm.sade.haku.oppija.hakemus.it;

import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationDAOMongoImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ApplicationDaoIT extends IntegrationTestSupport {
    @Test
    public void fetchApplication() throws IOException {
        final ApplicationDAOMongoImpl dao = appContext.getBean(ApplicationDAOMongoImpl.class);
        final List<Application> applications = dao.find(new Application());
        final Application application = applications.get(1);
        assertEquals("aho minna wa", application.getFullName());
    }
}

