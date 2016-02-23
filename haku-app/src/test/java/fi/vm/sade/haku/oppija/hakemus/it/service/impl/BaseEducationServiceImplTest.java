package fi.vm.sade.haku.oppija.hakemus.it.service.impl;

import fi.vm.sade.haku.oppija.common.suoritusrekisteri.impl.SuoritusrekisteriServiceMockImpl;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.it.IntegrationTestSupport;
import fi.vm.sade.haku.oppija.hakemus.service.BaseEducationService;
import fi.vm.sade.haku.oppija.hakemus.service.impl.BaseEducationServiceImpl;
import fi.vm.sade.haku.oppija.lomake.service.ApplicationSystemService;
import fi.vm.sade.haku.oppija.lomake.service.impl.ApplicationSystemServiceImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BaseEducationServiceImplTest extends IntegrationTestSupport {
    private ApplicationSystemService mongoAppSystemService;

    @Before
    public void getMongoAppSystemService() throws IOException {
        mongoAppSystemService = appContext.getBean(ApplicationSystemServiceImpl.class);
    }

    @Test
    public void usesSuoritusrekisteriSendingSchoolIn2ndGradeHakuIfPeruskouluBaseeductaion() {
        Application application = getTestApplication("1.2.246.562.11.00004883579");
        assertEquals("Yhteishaku ammatilliseen ja lukioon, kevät 2016", mongoAppSystemService.getApplicationSystem(application.getApplicationSystemId()).getName().getText("fi"));
        BaseEducationService baseEducationService = new BaseEducationServiceImpl(
                new SuoritusrekisteriServiceMockImpl(),
                mongoAppSystemService);
        baseEducationService.addSendingSchool(application);
        assertEquals("oppilaitos" ,application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION).get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL));
    }

    @Test
    public void usesSendingSchoolFromHakemusInKKHakuIfNewYoBaseEducation() {
        Application application = getTestApplication("1.2.246.562.11.00004886042");
        assertEquals("Yhteishaku ammatilliseen ja lukioon, kevät 2016", mongoAppSystemService.getApplicationSystem(application.getApplicationSystemId()).getName().getText("fi"));
        BaseEducationService baseEducationService = new BaseEducationServiceImpl(
                new SuoritusrekisteriServiceMockImpl(),
                mongoAppSystemService);
        baseEducationService.addSendingSchool(application);
        assertEquals("1.2.246.562.10.53442802061" ,application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION).get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL));
    }
    @Test
    public void usesSuoritusrekisteriSendingSchoolInKKHaku() {
        Application application = getTestApplication("1.2.246.562.11.00004580445");
        assertEquals("Korkeakoulujen yhteishaku kevät 2015", mongoAppSystemService.getApplicationSystem(application.getApplicationSystemId()).getName().getText("fi"));
        BaseEducationService baseEducationService = new BaseEducationServiceImpl(
                new SuoritusrekisteriServiceMockImpl(),
                mongoAppSystemService);
        baseEducationService.addSendingSchool(application);
        assertEquals("oppilaitos" ,application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION).get(OppijaConstants.ELEMENT_ID_SENDING_SCHOOL));
    }

}
