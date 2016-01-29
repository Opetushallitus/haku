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
    public void testThatCheckingStopsWhenAutomaticEligibilityIsNotEnabledForHaku() {
        final ApplicationSystem as = createApplicationSystemWithEligibilities(false);
        final Ohjausparametrit ohjausparametrit = createOhjausparametritWithNoEligibilityTimestamp();

        final HakuService hakuService = Mockito.mock(HakuService.class);
        final StatusRepository statusRepository = Mockito.mock(StatusRepository.class);
        final OhjausparametritService ohjausparametritService = Mockito.mock(OhjausparametritService.class);

        runEligibilityCheckWithApplicationSystem(as, ohjausparametrit, hakuService, statusRepository, ohjausparametritService);

        Mockito.verify(ohjausparametritService, Mockito.times(0)).fetchOhjausparametritForHaku(Mockito.anyString());
        Mockito.verify(hakuService, Mockito.times(0)).getApplicationSystem(as.getId());
        Mockito.verify(statusRepository, Mockito.times(0)).startOperation(Mockito.anyString(),Mockito.anyString());
    }

    @Test
    public void testThatCheckingGoesThroughWhenEligibilityFromOhjausparametritIsNotSet() {
        final ApplicationSystem as = createApplicationSystemWithEligibilities(true);
        final Ohjausparametrit ohjausparametrit = createOhjausparametritWithNoEligibilityTimestamp();

        final HakuService hakuService = Mockito.mock(HakuService.class);
        final StatusRepository statusRepository = Mockito.mock(StatusRepository.class);
        final OhjausparametritService ohjausparametritService = Mockito.mock(OhjausparametritService.class);

        runEligibilityCheckWithApplicationSystem(as, ohjausparametrit, hakuService, statusRepository, ohjausparametritService);

        Mockito.verify(ohjausparametritService, Mockito.times(1)).fetchOhjausparametritForHaku(Mockito.anyString());
        Mockito.verify(hakuService, Mockito.times(1)).getApplicationSystem(as.getId());
        Mockito.verify(statusRepository, Mockito.times(1)).startOperation(Mockito.anyString(),Mockito.anyString());
    }

    @Test
    public void testThatCheckingStopsWhenEligibilityFromOhjausparametritIsSetAndOld() {
        final ApplicationSystem as = createApplicationSystemWithEligibilities(true);
        final Ohjausparametrit ohjausparametrit = createOhjausparametritWithOldEligibilityCheckTimestamp();

        final HakuService hakuService = Mockito.mock(HakuService.class);
        final StatusRepository statusRepository = Mockito.mock(StatusRepository.class);
        final OhjausparametritService ohjausparametritService = Mockito.mock(OhjausparametritService.class);

        runEligibilityCheckWithApplicationSystem(as, ohjausparametrit, hakuService, statusRepository, ohjausparametritService);

        Mockito.verify(ohjausparametritService, Mockito.times(1)).fetchOhjausparametritForHaku(Mockito.anyString());
        Mockito.verify(hakuService, Mockito.times(0)).getApplicationSystem(as.getId());
        Mockito.verify(statusRepository, Mockito.times(0)).startOperation(Mockito.anyString(),Mockito.anyString());
    }
    @Test
    public void testThatCheckingGoesThroughWhenEligibilityFromOhjausparametritIsSetAndStillValid() {
        final ApplicationSystem as = createApplicationSystemWithEligibilities(true);
        final Ohjausparametrit ohjausparametrit = createOhjausparametritWithValidEligibilityCheckTimestamp();

        final HakuService hakuService = Mockito.mock(HakuService.class);
        final StatusRepository statusRepository = Mockito.mock(StatusRepository.class);
        final OhjausparametritService ohjausparametritService = Mockito.mock(OhjausparametritService.class);

        runEligibilityCheckWithApplicationSystem(as, ohjausparametrit, hakuService, statusRepository, ohjausparametritService);

        Mockito.verify(ohjausparametritService, Mockito.times(1)).fetchOhjausparametritForHaku(Mockito.anyString());
        Mockito.verify(hakuService, Mockito.times(1)).getApplicationSystem(as.getId());
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

    private static ApplicationSystem createApplicationSystemWithEligibilities(boolean automaticEligibilityInUse) {
        ApplicationSystem as = new ApplicationSystem("hakuOid", null, new I18nText(ImmutableMap.of("fi","hakuWithEligibilities")), "JULKAISTU",
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, automaticEligibilityInUse, Collections.newArrayList("aosWithEligibility"), null, false);
        return as;
    }

    private static void runEligibilityCheckWithApplicationSystem(ApplicationSystem as, Ohjausparametrit ohjausparametrit, HakuService hakuService, StatusRepository statusRepository, OhjausparametritService ohjausparametritService) {
        final SuoritusrekisteriService suoritusrekisteriService = Mockito.mock(SuoritusrekisteriService.class);
        final ApplicationDAO applicationDAO = Mockito.mock(ApplicationDAO.class);
        EligibilityCheckWorkerImpl eligibilityCheckWorker = new EligibilityCheckWorkerImpl(suoritusrekisteriService, hakuService, applicationDAO, statusRepository, ohjausparametritService);
        Mockito.when(ohjausparametritService.fetchOhjausparametritForHaku(Mockito.anyString())).thenReturn(ohjausparametrit);
        Mockito.when(hakuService.getApplicationSystems(Mockito.anyBoolean())).thenReturn(Collections.newArrayList(as));
        Mockito.when(hakuService.getApplicationSystem(as.getId())).thenReturn(as);
        eligibilityCheckWorker.checkEligibilities(null);
    }

}
