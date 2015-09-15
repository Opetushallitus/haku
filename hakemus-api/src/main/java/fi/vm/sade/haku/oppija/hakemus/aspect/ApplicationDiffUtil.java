package fi.vm.sade.haku.oppija.hakemus.aspect;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.Change;
import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;

import java.util.*;

public final class ApplicationDiffUtil {

    public static final String FIELD = "field";
    public static final String OLD_VALUE = "old value";
    public static final String NEW_VALUE = "new value";

    public static List<Map<String, String>> addHistoryBasedOnChangedAnswers(final Application newApplication, final Application oldApplication, String userName, String reason) {
        Map<String, String> oldAnswers = oldApplication.getVastauksetMerged();
        Map<String, String> newAnswers = newApplication.getVastauksetMerged();
        List<Map<String, String>> answerChanges = mapsToChanges(oldAnswers, newAnswers);
        List<Map<String, String>> eligibilityChanges = ApplicationDiffUtil.oldAndNewEligibilitiesToListOfChanges(oldApplication.getPreferenceEligibilities(), newApplication.getPreferenceEligibilities());
        List<Map<String, String>> additionalInfoChanges = mapsToChanges(oldApplication.getAdditionalInfo(), newApplication.getAdditionalInfo());

        List<Map<String, String>> changes = new LinkedList<>();
        changes.addAll(answerChanges);
        changes.addAll(eligibilityChanges);
        changes.addAll(additionalInfoChanges);
        if (!changes.isEmpty()) {
            Change change = new Change(new Date(), userName, reason, changes);
            newApplication.addHistory(change);
        }
        return changes;
    }

    public static List<Map<String, String>> oldAndNewEligibilitiesToListOfChanges(List<PreferenceEligibility> oldEligibilities, List<PreferenceEligibility> newEligibilities) {
        Map<String, String> oldAsMap = new HashMap<>();
        Map<String, String> newAsMap = new HashMap<>();
        for (PreferenceEligibility e : oldEligibilities) {
            oldAsMap.put(auditLogKey(e), auditLogValue(e));
        }
        for (PreferenceEligibility e : newEligibilities) {
            newAsMap.put(auditLogKey(e), auditLogValue(e));
        }
        return mapsToChanges(oldAsMap, newAsMap);
    }

    public static List<Map<String, String>> mapsToChanges(Map<String, String> oldMap, Map<String, String> newMap) {
        MapDifference<String, String> diff = Maps.difference(oldMap, newMap);
        List<Map<String, String>> changes = new ArrayList<>();
        for (Map.Entry<String, String> entry : diff.entriesOnlyOnLeft().entrySet()) {
            Map<String, String> change = new HashMap<>();
            change.put(FIELD, entry.getKey());
            change.put(OLD_VALUE, entry.getValue());
            changes.add(change);
        }
        for (Map.Entry<String, MapDifference.ValueDifference<String>> entry : diff.entriesDiffering().entrySet()) {
            Map<String, String> change = new HashMap<>();
            change.put(FIELD, entry.getKey());
            change.put(OLD_VALUE, entry.getValue().leftValue());
            change.put(NEW_VALUE, entry.getValue().rightValue());
            changes.add(change);
        }
        for (Map.Entry<String, String> entry : diff.entriesOnlyOnRight().entrySet()) {
            Map<String, String> change = new HashMap<>();
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
        return e.getStatus() + ":" + e.getSource() + ":" + e.getRejectionBasis();
    }
}
