package fi.vm.sade.haku.upgrade;

import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil.addHistoryBasedOnChangedAnswers;
import static fi.vm.sade.haku.oppija.hakemus.domain.Application.*;

/**
 * Erottelee kaksoistutkinnon ja ensimmäisen ammatillisen tutkinnon
 * keskiarvotiedot toisistaan. Korjattavissa olevat hakemukset korjataan
 * automaattisesti. Hakemukset joiden alkuperäisestä merkityksestä ei voida
 * olla varmoja muutetaan vajaavaisiksi.
 */
public class Level4 {
    public static Application fixAmmatillisenKoulutuksenKeskiarvo(Application original, LoggerAspect loggerAspect) {
        // NOP if called directly with valid application - as is done by test cases
        if (!requiresPatch(original)) {
            return original;
        }
        Application modified = original.clone();
        modified.setModelVersion(4);
        final boolean repairable = isRepairable(original);
        if (repairable) {
            copyKeskiarvoFields(modified);
            removeKeskiarvoFields(modified);
        } else {
            emptyInvalidKeskiarvoFields(modified);
            copyKeskiarvoFields(modified);
            modified.setState(State.INCOMPLETE);
        }

        loggerAspect.logUpdateApplication(original,
                new ApplicationPhase(original.getApplicationSystemId(),
                        "osaaminen", modified.getPhaseAnswers("osaaminen")));

        return logOperation(original, modified, repairable
                ? "Korjattiin automaattisesti rikkinäinen ammatillisen koulutuksen keskiarvo."
                : "Poistettiin ammatillisen koulutuksen rikkinäisestä osaamisosiosta keskiarvo: \'"
                + original.getAnswers().get("osaaminen").get("keskiarvo") + "\'"
                + ", ja keskiarvon tutkintotieto: \'" + original.getAnswers().get("osaaminen").get("keskiarvo-tutkinto")
                + "\'. Korjattava manuaalisesti.");
    }

    private static void removeKeskiarvoFields(Application modified) {
        final List<String> keysToTransform = new ArrayList<String>() {{
            add("keskiarvo");
            add("arvosanaasteikko");
            add("keskiarvo-tutkinto");
        }};

        for(String key : keysToTransform) {
            modified.getAnswers().get("osaaminen").remove(key);
        }
    }

    private static void copyKeskiarvoFields(Application modified) {
        Map<String, String> osaaminen = modified.getAnswers().get("osaaminen");
        final List<String> keysToTransform = new ArrayList<String>() {{
            add("keskiarvo");
            add("arvosanaasteikko");
            add("keskiarvo-tutkinto");
        }};

        for(String key : keysToTransform) {
            if (osaaminen.containsKey(key)) {
                osaaminen.put(key + "_yo_ammatillinen", osaaminen.get(key));
            }
        }
    }

    private static void emptyInvalidKeskiarvoFields(Application modified) {
        modified.getAnswers().get("osaaminen").put("keskiarvo", "");
        modified.getAnswers().get("osaaminen").put("keskiarvo-tutkinto", "");
    }

    private static Application logOperation(Application original, Application modified, String note) {
        final String SYSTEM_USER = "järjestelmä";
        addHistoryBasedOnChangedAnswers(modified, original, SYSTEM_USER, note);
        modified.addNote(new ApplicationNote(note, new Date(), SYSTEM_USER));
        return modified;
    }

    private static boolean isRepairable(Application application) {
        final Map<String, Map<String, String>> answers = application.getAnswers();
        return !Boolean.parseBoolean(answers.get("koulutustausta").get("pohjakoulutus_am"));
    }

    public static boolean requiresPatch(Application application) {
        final Map<String, Map<String, String>> answers = application.getAnswers();
        final List<String> applicationSystemsToUpdate = new ArrayList<String>() {{
            add("1.2.246.562.29.95390561488");
            add("1.2.246.562.29.34166623859");
            add("1.2.246.562.29.12487482171");
            add("1.2.246.562.29.72771128187");
        }};
        return applicationSystemsToUpdate.contains(application.getApplicationSystemId()) &&
                answers.containsKey("koulutustausta") &&
                answers.containsKey("osaaminen") &&
                answers.get("koulutustausta").containsKey("pohjakoulutus_yo_ammatillinen") &&
                Boolean.parseBoolean(answers.get("koulutustausta").get("pohjakoulutus_yo_ammatillinen")) &&
                (
                    answers.get("osaaminen").containsKey("keskiarvo") ||
                    answers.get("osaaminen").containsKey("arvosanaasteikko") ||
                    answers.get("osaaminen").containsKey("keskiarvo-tutkinto")
                ) &&
                !(
                        answers.get("osaaminen").containsKey("keskiarvo_yo_ammatillinen") ||
                        answers.get("osaaminen").containsKey("arvosanaasteikko_yo_ammatillinen") ||
                        answers.get("osaaminen").containsKey("keskiarvo-tutkinto_yo_ammatillinen")
                );
    }
}
