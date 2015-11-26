package fi.vm.sade.haku.oppija.postprocess;

import com.google.common.collect.ImmutableMap;
import de.flapdoodle.embed.process.collections.Collections;
import fi.vm.sade.haku.healthcheck.StatusRepository;
import fi.vm.sade.haku.oppija.common.suoritusrekisteri.SuoritusrekisteriService;
import fi.vm.sade.haku.oppija.hakemus.it.dao.ApplicationDAO;
import fi.vm.sade.haku.oppija.lomake.domain.*;
import fi.vm.sade.haku.oppija.postprocess.impl.EligibilityCheckWorkerImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.OhjausparametritService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.domain.Ohjausparametri;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.ohjausparametrit.domain.Ohjausparametrit;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EligibilityCheckWorkerImplTest {

    @Test
    public void testThatCheckingGoesThroughWhenEligibilityFromOhjausparametritIsNull() {
        final SuoritusrekisteriService suoritusrekisteriService = Mockito.mock(SuoritusrekisteriService.class);
        final HakuService hakuService = Mockito.mock(HakuService.class);
        final ApplicationDAO applicationDAO = Mockito.mock(ApplicationDAO.class);
        final StatusRepository statusRepository = Mockito.mock(StatusRepository.class);
        final OhjausparametritService ohjausparametritService = Mockito.mock(OhjausparametritService.class);
        EligibilityCheckWorkerImpl eligibilityCheckWorker = new EligibilityCheckWorkerImpl(suoritusrekisteriService, hakuService, applicationDAO, statusRepository, ohjausparametritService);
        Mockito.when(ohjausparametritService.fetchOhjausparametritForHaku(Mockito.anyString())).thenReturn(createOhjausparametritWithNoEligibilityTimestamp());
        Mockito.when(hakuService.getApplicationSystems(Mockito.anyBoolean())).thenReturn(Collections.newArrayList(createApplicationSystemWithEligibilities()));
        eligibilityCheckWorker.checkEligibilities(null);

        Mockito.verify(ohjausparametritService, Mockito.times(1)).fetchOhjausparametritForHaku(Mockito.anyString());
        Mockito.verify(statusRepository, Mockito.times(1)).startOperation(Mockito.anyString(),Mockito.anyString());
    }

    @Test
    public void testThatCheckingStopsWhenEligibilityFromOhjausparametritIsSetAndOld() {
        final SuoritusrekisteriService suoritusrekisteriService = Mockito.mock(SuoritusrekisteriService.class);
        final HakuService hakuService = Mockito.mock(HakuService.class);
        final ApplicationDAO applicationDAO = Mockito.mock(ApplicationDAO.class);
        final StatusRepository statusRepository = Mockito.mock(StatusRepository.class);
        final OhjausparametritService ohjausparametritService = Mockito.mock(OhjausparametritService.class);
        EligibilityCheckWorkerImpl eligibilityCheckWorker = new EligibilityCheckWorkerImpl(suoritusrekisteriService, hakuService, applicationDAO, statusRepository, ohjausparametritService);
        Mockito.when(ohjausparametritService.fetchOhjausparametritForHaku(Mockito.anyString())).thenReturn(createOhjausparametritWithOldEligibilityCheckTimestamp());
        Mockito.when(hakuService.getApplicationSystems(Mockito.anyBoolean())).thenReturn(Collections.newArrayList(createApplicationSystemWithEligibilities()));
        eligibilityCheckWorker.checkEligibilities(null);

        Mockito.verify(ohjausparametritService, Mockito.times(1)).fetchOhjausparametritForHaku(Mockito.anyString());
        Mockito.verify(statusRepository, Mockito.times(0)).startOperation(Mockito.anyString(),Mockito.anyString());
    }
    @Test
    public void testThatCheckingGoesThroughWhenEligibilityFromOhjausparametritIsSetAndStillValid() {
        final SuoritusrekisteriService suoritusrekisteriService = Mockito.mock(SuoritusrekisteriService.class);
        final HakuService hakuService = Mockito.mock(HakuService.class);
        final ApplicationDAO applicationDAO = Mockito.mock(ApplicationDAO.class);
        final StatusRepository statusRepository = Mockito.mock(StatusRepository.class);
        final OhjausparametritService ohjausparametritService = Mockito.mock(OhjausparametritService.class);
        EligibilityCheckWorkerImpl eligibilityCheckWorker = new EligibilityCheckWorkerImpl(suoritusrekisteriService, hakuService, applicationDAO, statusRepository, ohjausparametritService);
        Mockito.when(ohjausparametritService.fetchOhjausparametritForHaku(Mockito.anyString())).thenReturn(createOhjausparametritWithValidEligibilityCheckTimestamp());
        Mockito.when(hakuService.getApplicationSystems(Mockito.anyBoolean())).thenReturn(Collections.newArrayList(createApplicationSystemWithEligibilities()));
        eligibilityCheckWorker.checkEligibilities(null);

        Mockito.verify(ohjausparametritService, Mockito.times(1)).fetchOhjausparametritForHaku(Mockito.anyString());
        Mockito.verify(statusRepository, Mockito.times(1)).startOperation(Mockito.anyString(),Mockito.anyString());
    }
    private static Ohjausparametrit createOhjausparametritWithNoEligibilityTimestamp() {
        return new Ohjausparametrit();
    }
    private static Ohjausparametrit createOhjausparametritWithValidEligibilityCheckTimestamp() {
        Ohjausparametrit o = new Ohjausparametrit();
        Ohjausparametri op = new Ohjausparametri();
        op.setDate(new Date(new Date().getTime() + TimeUnit.DAYS.toMillis(7)));
        o.setPH_AHP(op);
        return o;
    }
    private static Ohjausparametrit createOhjausparametritWithOldEligibilityCheckTimestamp() {
        Ohjausparametrit o = new Ohjausparametrit();
        Ohjausparametri op = new Ohjausparametri();
        op.setDate(new Date(new Date().getTime() - TimeUnit.DAYS.toMillis(7)));
        o.setPH_AHP(op);
        return o;
    }
    private static ApplicationSystem createApplicationSystemWithEligibilities() {
        ApplicationSystem as = new ApplicationSystem("hakuOid", null, new I18nText(ImmutableMap.of("fi","hakuWithEligibilities")), "JULKAISTU",
                null, null, null, null, null, null, null, null, null, null, null, null, null, Collections.newArrayList("aosWithEligibility"), null, false);
        return as;
    }
}
