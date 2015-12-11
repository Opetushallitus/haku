package fi.vm.sade.haku.oppija.hakemus.it.dao;

import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import fi.vm.sade.haku.oppija.common.dao.AbstractDAOTest;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultDTO;
import fi.vm.sade.haku.oppija.hakemus.domain.dto.ApplicationSearchResultItemDTO;
import fi.vm.sade.haku.oppija.hakemus.service.ApplicationService;
import fi.vm.sade.haku.util.JsonTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/tomcat-container-context.xml")
@ActiveProfiles(profiles = {"it"})
public class ApplicationSearchMongoIT extends AbstractDAOTest {
    public static final String SIRKUSALAN_PERUSTUKTINTO_PK_ID = "1.2.246.562.14.2014021310042661282381";
    public static final String SIRKUSALAN_PERUSTUKTINTO_PK_AOID = "398";
    public static final String TOINEN_ASTE_KEVAT_2014 = "1.2.246.562.5.2013080813081926341927";

    public static final String KOULUTUSKESKUS_SALPAUS_LAHTI_STALBERINKATU = "1.2.246.562.10.43328767116";
    public static final String KOULUTUSKESKUS_SALPAUS = "1.2.246.562.10.81934895871";
    public static final String PAIJAT_HAMEEN_KOUTUTUSKONSERNI = "1.2.246.562.10.594252633210";
    public static final String ROOT_ORGANIZATION = "1.2.246.562.10.00000000001";
    public static final String SIRKUSLAINEN_HETU = "KkXOzRby23Ww4tlmQFb/ynWM+4Z7oQOGOv5aO2rdpLk=";

    @Autowired
    @Qualifier("applicationDAOMongoImpl")
    private ApplicationDAO applicationDAO;

    @Autowired
    @Qualifier("applicationServiceImpl")
    private ApplicationService applicationService;

    private ApplicationFilterParameters koulutuskeskusSalpausOrganization;

    @Before
    public void setUp() throws Exception {
        ArrayList<String> org = Lists.newArrayList(KOULUTUSKESKUS_SALPAUS);
        koulutuskeskusSalpausOrganization = new ApplicationFilterParameters(5, org, org, org, null, null);

        DBObject applicationFromJson = JsonTestData.readTestData("harkinnanvarainen_salpaus_ei_sirkus.json");
        mongoTemplate.getCollection(getCollectionName()).insert(applicationFromJson);

        Application savedApplication = applicationDAO.find(new Application((String) applicationFromJson.get("oid"))).get(0);
        applicationDAO.save(cloneAndChangeSalpausKohdeToSalpausSirkus(savedApplication));
    }

    public Application cloneAndChangeSalpausKohdeToSalpausSirkus(Application application) throws IOException {
        Application applicationToSalpausSirkus = application.clone().setOid("1.2.246.562.11.00000840423");

        Map<String, String> hakutoiveet = applicationToSalpausSirkus.getAnswers().get("hakutoiveet");
        hakutoiveet.put("preference2-Koulutus-id-aoIdentifier", SIRKUSALAN_PERUSTUKTINTO_PK_AOID);
        hakutoiveet.put("preference2-Koulutus-id", SIRKUSALAN_PERUSTUKTINTO_PK_ID);
        hakutoiveet.put("preference2-Koulutus", "Sirkusalan perustutkinto, pk");
        hakutoiveet.put("preference2-Koulutus-id-educationcode", "koulutus_321902");
        hakutoiveet.put("preference2-Opetuspiste-id", KOULUTUSKESKUS_SALPAUS_LAHTI_STALBERINKATU);
        hakutoiveet.put("preference2-Opetuspiste-id-parents", KOULUTUSKESKUS_SALPAUS_LAHTI_STALBERINKATU + ","+
                                                              KOULUTUSKESKUS_SALPAUS + "," +
                                                              PAIJAT_HAMEEN_KOUTUTUSKONSERNI + "," +
                                                              ROOT_ORGANIZATION);
        hakutoiveet.put("preference2-discretionary", "true");
        applicationToSalpausSirkus.setVaiheenVastauksetAndSetPhaseId("hakutoiveet", hakutoiveet);

        Map<String, String> henkilotiedot = applicationToSalpausSirkus.getAnswers().get("henkilotiedot");
        henkilotiedot.put("Henkilotunnus", SIRKUSLAINEN_HETU);
        henkilotiedot.put("Henkilotunnus_plain", "221158-956V");
        applicationToSalpausSirkus.setVaiheenVastauksetAndSetPhaseId("henkilotiedot", henkilotiedot);

        return applicationService.updateAuthorizationMeta(applicationToSalpausSirkus);
    }


    @Test
    public void testFindSameOrganisationWithDifferentApplicationOptionDiscretionaryOnlySearchOVT7698() throws Exception {
        ApplicationQueryParameters applicationQueryParameters  = new ApplicationQueryParametersBuilder()
                                                                            .setSearchTerms("")
                                                                            .setAsId(TOINEN_ASTE_KEVAT_2014)
                                                                            .setAoId(SIRKUSALAN_PERUSTUKTINTO_PK_AOID)
                                                                            .setLopOid(PAIJAT_HAMEEN_KOUTUTUSKONSERNI)
                                                                            .setDiscretionaryOnly(true).build();

        ApplicationSearchResultDTO result = applicationDAO.findAllQueried(applicationQueryParameters, koulutuskeskusSalpausOrganization);
        assertEquals(1, result.getTotalCount());
        ApplicationSearchResultItemDTO application = result.getResults().get(0);
        assertEquals(SIRKUSLAINEN_HETU.toUpperCase(), application.getSsn());
    }
    @Override
    protected String getCollectionName() {
        return "application";
    }
}
