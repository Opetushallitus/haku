package fi.vm.sade.haku.oppija.postprocess.upgrade;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.domain.Change;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.User;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration.FeatureFlag;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import fi.vm.sade.hakutest.IntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration.FeatureFlag.erotteleAmmatillinenJaYoAmmatillinenKeskiarvo;
import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.domain.FormConfiguration.FeatureFlag.kansainvalinenYoAmkKysymys;
import static fi.vm.sade.hakutest.TestHelpers.Tuple.tuple;
import static fi.vm.sade.hakutest.TestHelpers.list;
import static fi.vm.sade.hakutest.TestHelpers.map;
import static fi.vm.sade.hakutest.TestHelpers.mapWithoutNullValues;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class Level4Test extends IntegrationTest {
    private LoggerAspect loggerAspect = mock(LoggerAspect.class);
    private static final String AFFECTED_APPLICATION_SYSTEM_ID = "1.2.246.562.29.95390561488";
    private static final List<Map<String, String>> kaksoistutkintoChangeList = list(
        map("field", "arvosanaasteikko",
            "old value", "1-3"),
        map("field", "keskiarvo",
            "old value", "1,00"),
        map("field", "keskiarvo-tutkinto",
            "old value", "Kaksoistutkinto"),
        map("field", "keskiarvo_yo_ammatillinen",
            "new value", "1,00"),
        map("field", "keskiarvo-tutkinto_yo_ammatillinen",
            "new value", "Kaksoistutkinto"),
        map("field", "arvosanaasteikko_yo_ammatillinen",
            "new value", "1-3"));
    private static final Map<String, String> kaksoistutkintoOsaaminen = map(
            "keskiarvo_yo_ammatillinen", "1,00",
            "arvosanaasteikko_yo_ammatillinen", "1-3",
            "keskiarvo-tutkinto_yo_ammatillinen", "Kaksoistutkinto");

    private static Application mkApplication(final String applicationSystemId,
                                             final Boolean yoAm,
                                             final Boolean am,
                                             final String keskiarvo,
                                             final String arvosanaAsteikko,
                                             final String tutkinto) {
        Map<String, Map<String, String>> answers = map(
                tuple("koulutustausta", map(
                        "pohjakoulutus_yo_ammatillinen", yoAm.toString(),
                        "pohjakoulutus_am", am.toString())),
                tuple("osaaminen", mapWithoutNullValues(
                        "keskiarvo", keskiarvo,
                        "arvosanaasteikko", arvosanaAsteikko,
                        "keskiarvo-tutkinto", tutkinto)));
        return new Application(applicationSystemId, new User(User.ANONYMOUS_USER), answers, null);
    }

    @Before
    public void setup() {
        mongoServer.dropCollections();
    }

    @Test
    public void testInvalidation() throws Exception {
        Application application = mkApplication(AFFECTED_APPLICATION_SYSTEM_ID,
            true, true, "1,00", "1-3", "Ammatillinen ja kaksoistutkinto");

        Application fixed = Level4.fixAmmatillisenKoulutuksenKeskiarvo(application, loggerAspect);

        List<ApplicationNote> notes = fixed.getNotes();
        List expectedNotes = list(
            new ApplicationNote(
                "Poistettiin ammatillisen koulutuksen rikkinäisestä osaamisosiosta keskiarvo: '1,00', ja keskiarvon tutkintotieto: 'Ammatillinen ja kaksoistutkinto'. Korjattava manuaalisesti.",
                notes.get(0).getAdded(),
                "järjestelmä"));
        assertEquals(expectedNotes, notes);

        //assertEquals(fixed.getState(), State.INCOMPLETE);

        List<Change> history = fixed.getHistory();
        List expectedChanges = list(new Change(
            history.get(0).getModified(),
            "järjestelmä",
            "Poistettiin ammatillisen koulutuksen rikkinäisestä osaamisosiosta keskiarvo: '1,00', ja keskiarvon tutkintotieto: 'Ammatillinen ja kaksoistutkinto'. Korjattava manuaalisesti.",
            list(
                map("field", "keskiarvo",
                    "old value", "1,00",
                    "new value", ""),
                map("field", "keskiarvo-tutkinto",
                    "old value", "Ammatillinen ja kaksoistutkinto",
                    "new value", ""),
                map("field", "keskiarvo_yo_ammatillinen",
                    "new value", ""),
                map("field", "keskiarvo-tutkinto_yo_ammatillinen",
                    "new value", ""),
                map("field", "arvosanaasteikko_yo_ammatillinen",
                    "new value", "1-3"))));
        assertEquals(expectedChanges, history);

        Map expectedOsaaminen = map(
            "keskiarvo", "",
            "arvosanaasteikko", "1-3",
            "keskiarvo-tutkinto", "",
            "keskiarvo_yo_ammatillinen", "",
            "arvosanaasteikko_yo_ammatillinen", "1-3",
            "keskiarvo-tutkinto_yo_ammatillinen", "");
        assertEquals(expectedOsaaminen, fixed.getAnswers().get("osaaminen"));
    }

    @Test
    public void testNoOsaaminenYoMigration() throws Exception {
        Application application = mkApplication(AFFECTED_APPLICATION_SYSTEM_ID,
                true, false, null, null, null);

        Application fixed = Level4.fixAmmatillisenKoulutuksenKeskiarvo(application, loggerAspect);
        assertEquals(list(), fixed.getHistory());
        assertEquals(list(), fixed.getNotes());
    }

    @Test
    public void testPartialOsaaminenYoMigration() throws Exception {
        Application application = mkApplication(AFFECTED_APPLICATION_SYSTEM_ID,
                true, false, "1,00", "1-3", null);

        Application fixed = Level4.fixAmmatillisenKoulutuksenKeskiarvo(application, loggerAspect);

        assertNull(fixed.getState());

        List<Change> history = fixed.getHistory();
        List expectedChanges = list(new Change(
                history.get(0).getModified(),
                "järjestelmä",
                "Korjattiin automaattisesti rikkinäinen ammatillisen koulutuksen keskiarvo.",
                list(
                    map("field", "arvosanaasteikko",
                        "old value", "1-3"),
                    map("field", "keskiarvo",
                        "old value", "1,00"),
                    map("field", "keskiarvo_yo_ammatillinen",
                        "new value", "1,00"),
                    map("field", "arvosanaasteikko_yo_ammatillinen",
                        "new value", "1-3"))));
        assertEquals(expectedChanges, history);

        List<ApplicationNote> notes = fixed.getNotes();
        List expectedNotes = list(
                new ApplicationNote(
                        "Korjattiin automaattisesti rikkinäinen ammatillisen koulutuksen keskiarvo.",
                        notes.get(0).getAdded(),
                        "järjestelmä"));
        assertEquals(expectedNotes, notes);
        assertEquals(map(
                        "keskiarvo_yo_ammatillinen", "1,00",
                        "arvosanaasteikko_yo_ammatillinen", "1-3"),
                fixed.getAnswers().get("osaaminen"));
    }

    @Test
    public void testYoMigration() throws Exception {
        Application application = mkApplication(AFFECTED_APPLICATION_SYSTEM_ID,
                true, false, "1,00", "1-3", "Kaksoistutkinto");

        Application fixed = Level4.fixAmmatillisenKoulutuksenKeskiarvo(application, loggerAspect);

        List<Change> history = fixed.getHistory();
        List expectedChanges = list(new Change(
                history.get(0).getModified(),
                "järjestelmä",
                "Korjattiin automaattisesti rikkinäinen ammatillisen koulutuksen keskiarvo.",
                kaksoistutkintoChangeList));
        assertEquals(expectedChanges, history);

        List<ApplicationNote> notes = fixed.getNotes();
        List expectedNotes = list(
            new ApplicationNote(
                "Korjattiin automaattisesti rikkinäinen ammatillisen koulutuksen keskiarvo.",
                notes.get(0).getAdded(),
                "järjestelmä"));
        assertEquals(expectedNotes, notes);
        assertEquals(kaksoistutkintoOsaaminen, fixed.getAnswers().get("osaaminen"));
    }

    @Test
    public void testAmMigration() throws Exception {
        Application application = mkApplication(AFFECTED_APPLICATION_SYSTEM_ID,
                false, true, "1,00", "1-3", "Ammatillinen");

        Application fixed = Level4.fixAmmatillisenKoulutuksenKeskiarvo(application, loggerAspect);

        assertEquals(0, fixed.getNotes().size());
        assertEquals(0, fixed.getHistory().size());

        Map expectedOsaaminen = map(
            "keskiarvo", "1,00",
            "arvosanaasteikko", "1-3",
            "keskiarvo-tutkinto", "Ammatillinen");
        assertEquals(expectedOsaaminen, fixed.getAnswers().get("osaaminen"));
    }

    @Test
    public void testFixWillBeAppliedOnlyOnce() {
        Application application = mkApplication(AFFECTED_APPLICATION_SYSTEM_ID,
                true, false, "1,00", "1-3", null);

        assertTrue(Level4.requiresPatch(application));
        Application fixed = Level4.fixAmmatillisenKoulutuksenKeskiarvo(application, loggerAspect);
        // Is cloned
        assertFalse(fixed == application);
        assertFalse(Level4.requiresPatch(fixed));
        // Same object if no patching needed
        assertTrue(fixed == Level4.fixAmmatillisenKoulutuksenKeskiarvo(fixed, loggerAspect));
    }

    private static List<Element> getOsaaminenQuestions(ApplicationSystem as) {
        return as.getForm().getChildren().get(3).getChildren().get(0).getChildren();
    }

    private static List<Element> getKansainvalinenYoKysymykset(ApplicationSystem as) {
        return getOsaaminenQuestions(as).get(1).getChildren();
    }

    private static List<Element> getYoKysymykset(ApplicationSystem as) {
        return getOsaaminenQuestions(as).get(2).getChildren();
    }

    private static List<Element> getAmKysymykset(ApplicationSystem as) {
        return getOsaaminenQuestions(as).get(3).getChildren();
    }

    @Test
    public void testNewFormGenerationUpgradesQuestionIds() {
        applicationSystemService.save(formGenerator.generate(AFFECTED_APPLICATION_SYSTEM_ID));
        assertEquals(true, formConfigurationDAO.findByApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID).getFeatureFlag(erotteleAmmatillinenJaYoAmmatillinenKeskiarvo));

        ApplicationSystem as = applicationSystemService.getApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID);

        List<Element> yoKansainvalinenAmmatillinen = getKansainvalinenYoKysymykset(as);
        assertEquals("osaaminen-kansainvalinenyo-arvosanat", yoKansainvalinenAmmatillinen.get(0).getId());
        assertEquals(OppijaConstants.ELEMENT_ID_OSAAMINEN_YOARVOSANAT_PARAS_KIELI, yoKansainvalinenAmmatillinen.get(0).getChildren().get(0).getId());

        List<Element> yoAmmatillinen = getYoKysymykset(as);
        assertEquals("keskiarvo_yo_ammatillinen", yoAmmatillinen.get(0).getId());
        assertEquals("arvosanaasteikko_yo_ammatillinen", yoAmmatillinen.get(1).getId());
        assertEquals("keskiarvo-tutkinto_yo_ammatillinen", yoAmmatillinen.get(2).getId());

        List<Element> ammatillinen = getAmKysymykset(as);
        assertEquals("keskiarvo", ammatillinen.get(0).getId());
        assertEquals("arvosanaasteikko", ammatillinen.get(1).getId());
        assertEquals("keskiarvo-tutkinto", ammatillinen.get(2).getId());
    }

    @Test
    public void testOldFormGenerationDoesNotUpgradeQuestionIds() {
        // Store configuration beforehand to see that re-generation will not overwrite it
        // Emulates old forms that will not be migrated but must still be re-generable
        Map<FeatureFlag, Boolean> flags = new HashMap<>();
        flags.put(erotteleAmmatillinenJaYoAmmatillinenKeskiarvo, false);
        flags.put(kansainvalinenYoAmkKysymys, true);
        formConfigurationDAO.save(new FormConfiguration(AFFECTED_APPLICATION_SYSTEM_ID, FormConfiguration.FormTemplateType.YHTEISHAKU_KEVAT_KORKEAKOULU, flags));
        applicationSystemService.save(formGenerator.generate(AFFECTED_APPLICATION_SYSTEM_ID));
        assertEquals(false, formConfigurationDAO.findByApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID).getFeatureFlag(erotteleAmmatillinenJaYoAmmatillinenKeskiarvo));

        ApplicationSystem as = applicationSystemService.getApplicationSystem(AFFECTED_APPLICATION_SYSTEM_ID);

        List<Element> yoAmmatillinen = getYoKysymykset(as);
        assertEquals("keskiarvo", yoAmmatillinen.get(0).getId());
        assertEquals("arvosanaasteikko", yoAmmatillinen.get(1).getId());
        assertEquals("keskiarvo-tutkinto", yoAmmatillinen.get(2).getId());

        List<Element> ammatillinen = getAmKysymykset(as);
        assertEquals("keskiarvo", ammatillinen.get(0).getId());
        assertEquals("arvosanaasteikko", ammatillinen.get(1).getId());
        assertEquals("keskiarvo-tutkinto", ammatillinen.get(2).getId());
    }

    @Test
    public void testScheduling() throws Exception {
        final String v3NoPatchOid = applicationOidService.generateNewOid();
        final String v3PatchOid = applicationOidService.generateNewOid();
        final String v4NoPatchOid = applicationOidService.generateNewOid();
        ApplicationSystem as = formGenerator.generate(AFFECTED_APPLICATION_SYSTEM_ID);
        applicationSystemService.save(as);

        Application v3NoPatchRequired = mkApplication(AFFECTED_APPLICATION_SYSTEM_ID, false, false, "1,00", "1-3", "Ei-korjattava");
        v3NoPatchRequired.setOid(v3NoPatchOid);
        v3NoPatchRequired.setModelVersion(3);
        applicationDAO.save(v3NoPatchRequired);

        Application v3PatchRequired = mkApplication(AFFECTED_APPLICATION_SYSTEM_ID, true, false, "1,00", "1-3", "Kaksoistutkinto");
        v3PatchRequired.setOid(v3PatchOid);
        v3PatchRequired.setModelVersion(3);
        applicationDAO.save(v3PatchRequired);

        // Luo uudenlainen v4 hakemus
        Application v4NoPatchRequired = Level4.fixAmmatillisenKoulutuksenKeskiarvo(v3PatchRequired, loggerAspect);
        v4NoPatchRequired.getHistory().clear();
        v4NoPatchRequired.getNotes().clear();
        v4NoPatchRequired.setOid(v4NoPatchOid);
        applicationDAO.save(v4NoPatchRequired);

        scheduler.setRun(true);
        scheduler.setRunModelUpgrade(true);
        scheduler.runProcess();
        scheduler.runIdentification();
        scheduler.runModelUpgrade();

        assertNotPatched(v3NoPatchRequired, applicationService.getApplicationByOid(v3NoPatchOid));

        Application v3Patch = applicationService.getApplicationByOid(v3PatchOid);
        //assertEquals((Integer)4, v3Patch.getModelVersion());
        List<Change> history = v3Patch.getHistory();
        List expectedChanges = list(new Change(
                history.get(0).getModified(),
                "järjestelmä",
                "Korjattiin automaattisesti rikkinäinen ammatillisen koulutuksen keskiarvo.",
                kaksoistutkintoChangeList));
        assertEquals(expectedChanges, history);
        List<ApplicationNote> notes = v3Patch.getNotes();
        List expectedNotes = list(
                new ApplicationNote(
                        "Korjattiin automaattisesti rikkinäinen ammatillisen koulutuksen keskiarvo.",
                        notes.get(0).getAdded(),
                        "järjestelmä"));
        assertEquals(expectedNotes, notes);
        assertEquals(kaksoistutkintoOsaaminen, v3Patch.getAnswers().get("osaaminen"));

        assertNotPatched(v4NoPatchRequired, applicationService.getApplicationByOid(v4NoPatchOid));

        scheduler.redoPostprocess();

        DBCursor c = mongoTemplate.getCollection("auditlog").find(
                QueryBuilder.start("_target").is("hakemus: " + v3PatchOid + ", vaihe: osaaminen").get());
        assertEquals(1, c.size());

        DBObject logEntry = c.iterator().next();
        assertEquals("hakemus: " + v3PatchOid + ", vaihe: osaaminen", logEntry.get("_target"));
        assertEquals("Hakemuksen muokkaus", logEntry.get("_type"));
        assertEquals("järjestelmä", logEntry.get("_user"));
        assertEquals("järjestelmä", logEntry.get("_userActsForUser"));
        assertEquals(
            map("arvosanaasteikko_yo_ammatillinen_new", "1-3",
                "keskiarvo_old", "1,00",
                "keskiarvo-tutkinto_yo_ammatillinen_new", "Kaksoistutkinto",
                "arvosanaasteikko_old", "1-3",
                "keskiarvo-tutkinto_old", "Kaksoistutkinto",
                "keskiarvo_yo_ammatillinen_new", "1,00"),
            logEntry.get("values"));
    }

    private void assertNotPatched(Application original, Application stored) {
        //assertEquals((Integer) 4, stored.getModelVersion());
        assertEquals(original.getAnswers(), stored.getAnswers());
        assertEquals(0, stored.getHistory().size());
        assertEquals(0, stored.getNotes().size());
    }

}
