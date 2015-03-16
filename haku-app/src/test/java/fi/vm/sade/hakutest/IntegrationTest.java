package fi.vm.sade.hakutest;

import fi.vm.sade.haku.oppija.configuration.MongoServer;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.yksilointi.Scheduler;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.FormConfigurationDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGenerator;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/tomcat-container-context.xml")
@ActiveProfiles(profiles = {"it"})
@Ignore
public class IntegrationTest {
    @Autowired
    protected ApplicationDAO applicationDAO;

    @Autowired
    protected FormConfigurationDAO formConfigurationDAO;

    @Autowired
    protected Scheduler scheduler;

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected MongoServer mongoServer;

    @Autowired
    protected FormGenerator formGenerator;

    @Autowired
    protected ApplicationSystemService applicationSystemService;

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected ApplicationOidService applicationOidService;
}
