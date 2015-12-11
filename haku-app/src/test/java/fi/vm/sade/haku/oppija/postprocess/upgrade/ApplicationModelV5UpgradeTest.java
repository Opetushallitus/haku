package fi.vm.sade.haku.oppija.postprocess.upgrade;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.testfixtures.MongoFixtureImporter;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.KoodistoServiceMockImpl;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.postprocess.upgrade.ApplicationModelV5Upgrade.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public final class ApplicationModelV5UpgradeTest {

    private static final String baseApplicationId = "00000877699";
    private final LoggerAspect loggerAspect = mock(LoggerAspect.class);
    private final KoodistoService koodistoService = new KoodistoServiceMockImpl();

    private static final String KOULU_AMMATTI_ID = "1.2.246.562.10.57118763500";
    private static final String TUTKINTO_AMMATTI_ID = "400000";

    private final Application baseApplication;

    private final ApplicationModelV5Upgrade applicationModelV5Upgrade;

    private final Map<String,String> brokenAll;
    private final Map<String,String> brokenYo = ImmutableMap.of(YO_AMMATILLINEN_OPPILAITOS, "yo-laitos", YO_AMMATILLINEN_TUTKINTO, "yo-tutkinto");
    private final Map<String,String> brokenAm1 = ImmutableMap.of(AM_OPPILAITOS_PREFIX, "am-laitos", AM_TUTKINTO_PREFIX, "am-tutkinto");
    private final Map<String,String> partialBrokenAm2_laitos = ImmutableMap.of(AM_OPPILAITOS_PREFIX+2, "am-laitos");
    private final Map<String,String> partialBrokenAm2_tutkinto = ImmutableMap.of(AM_TUTKINTO_PREFIX+2, "am-tutkinto");
    private final Map<String,String> okAll = new ImmutableMap.Builder<String,String>()
            .put(YO_AMMATILLINEN_OPPILAITOS, KOULU_AMMATTI_ID)
            .put( YO_AMMATILLINEN_TUTKINTO, TUTKINTO_AMMATTI_ID)
            .put(AM_OPPILAITOS_PREFIX, KOULU_AMMATTI_ID)
            .put(AM_TUTKINTO_PREFIX, TUTKINTO_AMMATTI_ID)
            .put(AM_OPPILAITOS_PREFIX+2, KOULU_AMMATTI_ID)
            .put(AM_TUTKINTO_PREFIX+2, TUTKINTO_AMMATTI_ID)
            .put(AM_OPPILAITOS_PREFIX+3, KOULU_AMMATTI_ID)
            .put(AM_TUTKINTO_PREFIX+3, TUTKINTO_AMMATTI_ID)
            .build();

    private final Map<String,String> muuFields = new ImmutableMap.Builder<String,String>()
            .put(YO_AMMATILLINEN_OPPILAITOS, YO_AMMATILLINEN_OPPILAITOS+MUU_POSTFIX)
            .put(YO_AMMATILLINEN_TUTKINTO, YO_AMMATILLINEN_TUTKINTO+MUU_POSTFIX)
            .put(AM_OPPILAITOS_PREFIX, AM_OPPILAITOS_PREFIX+MUU_POSTFIX)
            .put(AM_TUTKINTO_PREFIX, AM_TUTKINTO_PREFIX+MUU_POSTFIX)
            .put(AM_OPPILAITOS_PREFIX+2, AM_OPPILAITOS_PREFIX+MUU_POSTFIX +2)
            .put(AM_TUTKINTO_PREFIX+2, AM_TUTKINTO_PREFIX+MUU_POSTFIX +2)
            .put(AM_OPPILAITOS_PREFIX+3, AM_OPPILAITOS_PREFIX+MUU_POSTFIX +3)
            .put(AM_TUTKINTO_PREFIX + 3, AM_TUTKINTO_PREFIX+MUU_POSTFIX +3)
            .build();

    public ApplicationModelV5UpgradeTest() throws IOException {
        applicationModelV5Upgrade = new ApplicationModelV5Upgrade(koodistoService, loggerAspect);
        this.baseApplication = MongoFixtureImporter.getApplicationFixture(baseApplicationId);
        baseApplication.setApplicationSystemId(ApplicationModelV5Upgrade.APPLICABLE_APPLICATIONSYSTEM_ID);
        brokenAll = new HashMap<String, String>();
        brokenAll.putAll(brokenYo);
        brokenAll.putAll(brokenAm1);
        brokenAll.putAll(partialBrokenAm2_laitos);
        brokenAll.putAll(partialBrokenAm2_tutkinto);
    }

    private Application createApplicationWith(Map<String,String> answerChanges){
        final Application application = baseApplication.clone();
        Map<String,String> phaseAnwsers = new HashMap(application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION));
        phaseAnwsers.putAll(answerChanges);
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_EDUCATION, phaseAnwsers);
        return application;
    }

    @Test
    public void wrongAs() {
        final Application application = createApplicationWith(brokenAll);
        application.setApplicationSystemId("Other");
        noInteractions(application);
    }

    @Test
    public void okAll() {
        final Application application = createApplicationWith(okAll);
        noInteractions(application);
    }

    @Test
    public void fixYo() {
        final Application application = createApplicationWith(brokenYo);
        UpgradeResult<Application> result = applicationModelV5Upgrade.processUpgrade(application.clone());
        hasInteractions(application, result);
        checkChanges(application, result.getUpgradedDocument(), YO_AMMATILLINEN_OPPILAITOS, YO_AMMATILLINEN_TUTKINTO);
    }

    @Test
    public void fixAm1of1() {
        final Application application = createApplicationWith(brokenAm1);
        UpgradeResult<Application> result = applicationModelV5Upgrade.processUpgrade(application.clone());
        hasInteractions(application, result);

        checkChanges(application, result.getUpgradedDocument(), AM_OPPILAITOS_PREFIX, AM_TUTKINTO_PREFIX);
    }

    @Test
    public void fixAm2TutkintoOnly() {
        final Map<String,String> overrides = new HashMap(okAll);
        overrides.putAll(partialBrokenAm2_tutkinto);
        final Application application = createApplicationWith(overrides);

        UpgradeResult<Application> result = applicationModelV5Upgrade.processUpgrade(application.clone());
        hasInteractions(application, result);

        checkChanges(application, result.getUpgradedDocument(), AM_TUTKINTO_PREFIX+2);
    }

    @Test
    public void fixAm2KouluOnly() {
        final Map<String,String> overrides = new HashMap(okAll);
        overrides.putAll(partialBrokenAm2_laitos);
        final Application application = createApplicationWith(overrides);

        UpgradeResult<Application> result = applicationModelV5Upgrade.processUpgrade(application.clone());
        hasInteractions(application, result);

        checkChanges(application, result.getUpgradedDocument(), AM_OPPILAITOS_PREFIX+2);
    }

    @Test
    public void fixYoAndAmOk() {
        final Map<String,String> overrides = new HashMap(okAll);
        overrides.putAll(brokenYo);
        final Application application = createApplicationWith(overrides);

        UpgradeResult<Application> result = applicationModelV5Upgrade.processUpgrade(application.clone());
        hasInteractions(application, result);

        checkChanges(application, result.getUpgradedDocument(), YO_AMMATILLINEN_OPPILAITOS, YO_AMMATILLINEN_TUTKINTO);
    }


    @Test
    public void fixAllBroken() {
        final Map<String,String> overrides = new HashMap(okAll);
        overrides.putAll(brokenAll);
        final Application application = createApplicationWith(overrides);

        UpgradeResult<Application> result = applicationModelV5Upgrade.processUpgrade(application.clone());
        hasInteractions(application, result);

        checkChanges(application, result.getUpgradedDocument(), YO_AMMATILLINEN_OPPILAITOS, YO_AMMATILLINEN_TUTKINTO, AM_OPPILAITOS_PREFIX, AM_TUTKINTO_PREFIX, AM_OPPILAITOS_PREFIX+2, AM_TUTKINTO_PREFIX+2);
    }

    @Test
    public void doubleRun() {
        final Map<String,String> overrides = new HashMap(okAll);
        overrides.putAll(brokenAll);
        final Application application = createApplicationWith(overrides);

        UpgradeResult<Application> result = applicationModelV5Upgrade.processUpgrade(application.clone());
        hasInteractions(application, result);

        checkChanges(application, result.getUpgradedDocument(), YO_AMMATILLINEN_OPPILAITOS, YO_AMMATILLINEN_TUTKINTO, AM_OPPILAITOS_PREFIX, AM_TUTKINTO_PREFIX, AM_OPPILAITOS_PREFIX+2, AM_TUTKINTO_PREFIX+2);

        noInteractions(result.getUpgradedDocument());
    }
    
    private void noInteractions(final Application application) {
        final UpgradeResult<Application> result = applicationModelV5Upgrade.processUpgrade(application.clone());
        assertEquals("Application has changed", application, result.getUpgradedDocument());
        assertFalse(result.isModified());
        verifyZeroInteractions(loggerAspect);
    }

    private void hasInteractions(final Application original, UpgradeResult<Application> result) {
        assertNotEquals("Application has not changed", original, result.getUpgradedDocument());
        assertTrue(result.isModified());
        verify(loggerAspect).logUpdateApplication(eq(original), (ApplicationPhase) any());
    }

    private void checkChanges(final Application original, final Application modified, String... changedFields){

        List<Map<String, String>> changes = modified.getHistory().get(0).getChanges();
        assertEquals(changedFields.length*2, changes.size());
        Map<String,String> originalAnswers = original.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
        Map<String,String> answers = modified.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
        List<String> changeFieldsList = new ArrayList<String>();
        for (String field: changedFields){
            String answer = answers.get(field);
            assertTrue(answer + " Not expected", OppijaConstants.OPPILAITOS_TUNTEMATON.equals(answer) ||OppijaConstants.TUTKINTO_MUU.equals(answer));
            changeFieldsList.add(field);
            final String muuField = muuFields.get(field);
            assertTrue("old value missing for field: " + muuField, answers.containsKey(muuField));
            changeFieldsList.add(muuField);

            assertEquals("should match old value", originalAnswers.get(field), answers.get(muuField));
        }
        for (Map<String,String> change : changes){
            if (!changeFieldsList.remove(change.get("field")))
                fail("Change to field "+ change.get("field")+ " not expected");
        }
        assertTrue("ChangeFields should be empty", changeFieldsList.size() ==0 );
        assertTrue("Version should have been upgraded", applicationModelV5Upgrade.getTargetVersion()== modified.getModelVersion());


    }
}
