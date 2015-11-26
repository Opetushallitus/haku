package fi.vm.sade.hakutest;

import fi.vm.sade.haku.oppija.common.koulutusinformaatio.ApplicationOptionService;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.configuration.MongoServer;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationOidService;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.oppija.hakemus.service.SyntheticApplicationService;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.validation.ElementTreeValidator;
import fi.vm.sade.haku.oppija.postprocess.Scheduler;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.dao.FormConfigurationDAO;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGenerator;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.i18n.I18nBundleService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.OhjausparametritService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/tomcat-container-context.xml")
@ActiveProfiles(profiles = {"it"})
public abstract class IntegrationTest {
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
    protected ApplicationOptionService applicationOptionService;

    @Autowired
    protected SyntheticApplicationService syntheticApplicationService;

    @Autowired
    protected SuoritusrekisteriService suoritusrekisteriService;

    @Autowired
    protected HakuService hakuService;

    @Autowired
    protected ElementTreeValidator elementTreeValidator;

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected ApplicationOidService applicationOidService;

    @Autowired
    protected I18nBundleService i18nBundleService;

    @Autowired
    protected OhjausparametritService ohjausparametritService;
}
