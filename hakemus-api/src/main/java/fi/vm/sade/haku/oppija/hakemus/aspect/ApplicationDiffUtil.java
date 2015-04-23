package fi.vm.sade.haku.oppija.hakemus.aspect;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.Change;
import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ApplicationDiffUtil {

    public static final String FIELD = "field";
    public static final String OLD_VALUE = "old value";
    public static final String NEW_VALUE = "new value";

    private ApplicationDiffUtil() {
    }

    public static MapDifference<String, String> diffAnswers(final Application application, final ApplicationPhase applicationPhase) {
        return Maps.difference(application.getPhaseAnswers(applicationPhase.getPhaseId()), applicationPhase.getAnswers());
    }

    public static List<Map<String, String>> addHistoryBasedOnChangedAnswers(final Application newApplication, final Application oldApplication, String userName, String reason) {
        Map<String, String> oldAnswers = oldApplication.getVastauksetMerged();
        Map<String, String> newAnswers = newApplication.getVastauksetMerged();
        List<Map<String, String>> changes = ApplicationDiffUtil.oldAndNewAnswersToListOfChanges(oldAnswers, newAnswers);
        if (!changes.isEmpty()) {
            Change change = new Change(new Date(), userName, reason, changes);
            newApplication.addHistory(change);
        }
        return changes;
    }

    public static List<Map<String, String>> oldAndNewAnswersToListOfChanges(final Map<String, String> oldAnswers, final Map<String, String> newAnswers, String... skipKeys) {

        if (skipKeys != null) {
            for (String key : skipKeys) {
                oldAnswers.remove(key);
                newAnswers.remove(key);
            }
        }

        MapDifference<String, String> diffAnswers = Maps.difference(oldAnswers, newAnswers);

        ArrayList<Map<String, String>> changes = new ArrayList<Map<String, String>>();

        for (Map.Entry<String, String> entry : diffAnswers.entriesOnlyOnLeft().entrySet()) {
            Map<String, String> change = new HashMap<String, String>();
            change.put(FIELD, entry.getKey());
            change.put(OLD_VALUE, entry.getValue());
            changes.add(change);
        }

        for (Map.Entry<String, MapDifference.ValueDifference<String>> entry : diffAnswers.entriesDiffering().entrySet()) {
            Map<String, String> change = new HashMap<String, String>();
            change.put(FIELD, entry.getKey());
            change.put(OLD_VALUE, entry.getValue().leftValue());
            change.put(NEW_VALUE, entry.getValue().rightValue());
            changes.add(change);
        }

        for (Map.Entry<String, String> entry : diffAnswers.entriesOnlyOnRight().entrySet()) {
            Map<String, String> change = new HashMap<String, String>();
            change.put(FIELD, entry.getKey());
            change.put(NEW_VALUE, entry.getValue());
            changes.add(change);
        }
        return changes;
    }

    public static String auditLogKey(PreferenceEligibility e) {
        return "eligibility_" + e.getAoId().replaceAll("\\.", "_");
    }

    public static String auditLogValue(PreferenceEligibility e) {
        return e.getStatus().toString() + ":" + e.getSource().toString() + ":" + e.getRejectionBasis();
    }
}
