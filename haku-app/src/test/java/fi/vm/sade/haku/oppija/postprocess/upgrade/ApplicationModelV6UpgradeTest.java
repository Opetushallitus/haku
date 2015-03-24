package fi.vm.sade.haku.oppija.postprocess.upgrade;

import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.testfixtures.MongoFixtureImporter;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil.addHistoryBasedOnChangedAnswers;
import static fi.vm.sade.haku.oppija.postprocess.upgrade.ApplicationModelV5Upgrade.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public final class ApplicationModelV6UpgradeTest {

    private static final String baseApplicationId = "00000877699";
    private final LoggerAspect loggerAspect = mock(LoggerAspect.class);

    private static final String FREEHAND_AM_LAITOS = "am-laitos";
    private static final String FREEHAND_AM_TUTKINTO = "am-tutkinto";

    private final Application baseApplication;

    private final ApplicationModelV6Upgrade applicationModelV6Upgrade;

    public ApplicationModelV6UpgradeTest() throws IOException {
        applicationModelV6Upgrade = new ApplicationModelV6Upgrade(loggerAspect);
        this.baseApplication = MongoFixtureImporter.getApplicationFixture(baseApplicationId);
        baseApplication.setApplicationSystemId(ApplicationModelV6Upgrade.APPLICABLE_APPLICATIONSYSTEM_ID);
    }

    @Test
    public void doubleRun() {
        Application application = createApplication(true);

        UpgradeResult<Application> result = hasInteractions(application);
        checkChanges(result.getUpgradedDocument(), application.getHistory().size() + 1);

        application = result.getUpgradedDocument();
        result = noInteractions(result.getUpgradedDocument());
        checkChanges(result.getUpgradedDocument(), application.getHistory().size());
    }

    @Test
    public void fixMuuMuus() {
        final Application application = createApplication(true);
        UpgradeResult<Application> result = hasInteractions(application);
        checkChanges(result.getUpgradedDocument(), application.getHistory().size() + 1);
    }

    @Test
    public void doNotFix() {
        final Application application = createApplication(true);
        application.setApplicationSystemId("some_other");
        noInteractions(application);
    }

    @Test(expected = RuntimeException.class)
    public void testBrokenHistory() {
        final Application application = createApplication(false);
        UpgradeResult<Application> result = hasInteractions(application);
        checkChanges(result.getUpgradedDocument(), application.getHistory().size() + 1);
    }

    private Application createApplication(boolean createHistory) {
        final Application application = baseApplication.clone();
        // initial setup
        Map<String, String> phaseAnswers = new HashMap<>(application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION));
        phaseAnswers.put(AM_OPPILAITOS_PREFIX, OppijaConstants.OPPILAITOS_TUNTEMATON);
        phaseAnswers.put(AM_TUTKINTO_PREFIX, OppijaConstants.TUTKINTO_MUU);
        phaseAnswers.put(AM_OPPILAITOS_PREFIX + MUU_POSTFIX, FREEHAND_AM_LAITOS + 1);
        phaseAnswers.put(AM_TUTKINTO_PREFIX + MUU_POSTFIX, FREEHAND_AM_TUTKINTO + 1);
        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, phaseAnswers);
        // 1st run
        phaseAnswers.put(AM_OPPILAITOS_PREFIX + MUU_POSTFIX, OppijaConstants.OPPILAITOS_TUNTEMATON);
        phaseAnswers.put(AM_TUTKINTO_PREFIX + MUU_POSTFIX, OppijaConstants.TUTKINTO_MUU);
        phaseAnswers.put(AM_OPPILAITOS_PREFIX + MUU_POSTFIX + MUU_POSTFIX, FREEHAND_AM_LAITOS + 1);
        phaseAnswers.put(AM_TUTKINTO_PREFIX + MUU_POSTFIX + MUU_POSTFIX, FREEHAND_AM_TUTKINTO + 1);

        Application original = application.clone();
        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, phaseAnswers);
        if (createHistory)
            addHistoryBasedOnChangedAnswers(application, original, "järjestelmä", "Automaattikäsittely: ammatilliset pohjakoulutukset sidottu koodeihin ja vapaa selite siirretty.");
        // add more
        phaseAnswers.put(AM_OPPILAITOS_PREFIX + 2, OppijaConstants.OPPILAITOS_TUNTEMATON);
        phaseAnswers.put(AM_TUTKINTO_PREFIX + 2, OppijaConstants.TUTKINTO_MUU);
        phaseAnswers.put(AM_OPPILAITOS_PREFIX + MUU_POSTFIX + 2, FREEHAND_AM_LAITOS + 2);
        phaseAnswers.put(AM_TUTKINTO_PREFIX + MUU_POSTFIX + 2, FREEHAND_AM_TUTKINTO + 2);

        original = application.clone();
        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, phaseAnswers);
        if (createHistory)
            addHistoryBasedOnChangedAnswers(application, original, "käyttäjä", "Lisätty taustaan jotain");
        // 2nd run
        phaseAnswers.put(AM_OPPILAITOS_PREFIX + MUU_POSTFIX + MUU_POSTFIX, OppijaConstants.OPPILAITOS_TUNTEMATON);
        phaseAnswers.put(AM_TUTKINTO_PREFIX + MUU_POSTFIX + MUU_POSTFIX, OppijaConstants.TUTKINTO_MUU);
        phaseAnswers.put(AM_OPPILAITOS_PREFIX + MUU_POSTFIX + 2, OppijaConstants.OPPILAITOS_TUNTEMATON);
        phaseAnswers.put(AM_TUTKINTO_PREFIX + MUU_POSTFIX + 2, OppijaConstants.TUTKINTO_MUU);

        phaseAnswers.put(AM_OPPILAITOS_PREFIX + MUU_POSTFIX + MUU_POSTFIX + MUU_POSTFIX, FREEHAND_AM_LAITOS + 1);
        phaseAnswers.put(AM_TUTKINTO_PREFIX + MUU_POSTFIX + MUU_POSTFIX + MUU_POSTFIX, FREEHAND_AM_TUTKINTO + 1);
        phaseAnswers.put(AM_OPPILAITOS_PREFIX + MUU_POSTFIX + MUU_POSTFIX + 2, FREEHAND_AM_LAITOS + 2);
        phaseAnswers.put(AM_TUTKINTO_PREFIX + MUU_POSTFIX + MUU_POSTFIX + 2, FREEHAND_AM_TUTKINTO + 2);

        original = application.clone();
        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, phaseAnswers);
        if (createHistory)
            addHistoryBasedOnChangedAnswers(application, original, "järjestelmä", "Automaattikäsittely: ammatilliset pohjakoulutukset sidottu koodeihin ja vapaa selite siirretty.");
        return application;
    }

    private void checkChanges(final Application modified, int expectedHistorySize){
        assertEquals(expectedHistorySize, modified.getHistory().size());
        final Map<String, String> phaseAnswers = modified.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
        assertFalse("Extra key not removed", phaseAnswers.containsKey(AM_OPPILAITOS_PREFIX + MUU_POSTFIX + MUU_POSTFIX));
        assertFalse("Extra key not removed", phaseAnswers.containsKey(AM_TUTKINTO_PREFIX + MUU_POSTFIX + MUU_POSTFIX));
        assertFalse("Extra key not removed", phaseAnswers.containsKey(AM_OPPILAITOS_PREFIX + MUU_POSTFIX + MUU_POSTFIX + MUU_POSTFIX));
        assertFalse("Extra key not removed", phaseAnswers.containsKey(AM_TUTKINTO_PREFIX + MUU_POSTFIX + MUU_POSTFIX + MUU_POSTFIX));
        assertFalse("Extra key not removed", phaseAnswers.containsKey(AM_OPPILAITOS_PREFIX + MUU_POSTFIX + MUU_POSTFIX + 2));
        assertFalse("Extra key not removed", phaseAnswers.containsKey(AM_TUTKINTO_PREFIX + MUU_POSTFIX + MUU_POSTFIX + 2));
        assertTrue(phaseAnswers.get(AM_OPPILAITOS_PREFIX).equals(OppijaConstants.OPPILAITOS_TUNTEMATON));
        assertTrue(phaseAnswers.get(AM_TUTKINTO_PREFIX).equals(OppijaConstants.TUTKINTO_MUU));
        assertTrue(phaseAnswers.get(AM_OPPILAITOS_PREFIX + 2).equals(OppijaConstants.OPPILAITOS_TUNTEMATON));
        assertTrue(phaseAnswers.get(AM_TUTKINTO_PREFIX + 2).equals(OppijaConstants.TUTKINTO_MUU));
        assertTrue(phaseAnswers.get(AM_OPPILAITOS_PREFIX + MUU_POSTFIX).equals(FREEHAND_AM_LAITOS + 1));
        assertTrue(phaseAnswers.get(AM_TUTKINTO_PREFIX + MUU_POSTFIX).equals(FREEHAND_AM_TUTKINTO + 1));
        assertTrue(phaseAnswers.get(AM_OPPILAITOS_PREFIX + MUU_POSTFIX + 2).equals(FREEHAND_AM_LAITOS + 2));
        assertTrue(phaseAnswers.get(AM_TUTKINTO_PREFIX + MUU_POSTFIX + 2).equals(FREEHAND_AM_TUTKINTO + 2));
        assertEquals("expected model version is 6", Integer.valueOf(6), modified.getModelVersion());
    }

    private UpgradeResult<Application> noInteractions(final Application application) {
        final UpgradeResult<Application> result = applicationModelV6Upgrade.processUpgrade(application.clone());
        assertEquals("Application has changed", application, result.getUpgradedDocument());
        assertFalse(result.isModified());
        verifyZeroInteractions(loggerAspect);
        return result;
    }

    private UpgradeResult<Application>  hasInteractions(final Application original) {
        final UpgradeResult<Application> result = applicationModelV6Upgrade.processUpgrade(original.clone());
        assertNotEquals("Application has not changed", original, result.getUpgradedDocument());
        assertTrue(result.isModified());
        verify(loggerAspect).logUpdateApplication(eq(original), (ApplicationPhase) any());
        return result;
    }
}
