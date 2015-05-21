package fi.vm.sade.haku.oppija.hakemus.aspect;

import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.Change;
import fi.vm.sade.haku.oppija.hakemus.domain.PreferenceEligibility;

import java.util.*;

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
        List<Map<String, String>> answerChanges = ApplicationDiffUtil.oldAndNewAnswersToListOfChanges(oldAnswers, newAnswers);
        List<Map<String, String>> eligibilityChanges = ApplicationDiffUtil.oldAndNewEligibilitiesToListOfChanges(oldApplication.getPreferenceEligibilities(), newApplication.getPreferenceEligibilities());
        List<Map<String, String>> additionalInfoChanges = ApplicationDiffUtil.oldAndNewAdditinalInfoToListOfChanges(oldApplication.getAdditionalInfo(), newApplication.getAdditionalInfo());

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

    public static List<Map<String, String>> oldAndNewEligibilitiesToListOfChanges(List<PreferenceEligibility> oldEligibilities, List<PreferenceEligibility> newEligibilities) {
        List<Map<String, String>> changes = new ArrayList<>();

        for (PreferenceEligibility oldEligibility: oldEligibilities) {
            PreferenceEligibility e = getByAoId(newEligibilities, oldEligibility.getAoId());
            if (e == null) {
                Map<String, String> change = new HashMap<>();
                change.put(FIELD, auditLogKey(oldEligibility));
                change.put(OLD_VALUE, auditLogValue(oldEligibility));
                changes.add(change);
            } else {
                if (!auditLogValue(e).equals(auditLogValue(oldEligibility))) {
                    Map<String, String> change = new HashMap<>();
                    change.put(FIELD, auditLogKey(oldEligibility));
                    change.put(OLD_VALUE, auditLogValue(oldEligibility));
                    change.put(NEW_VALUE, auditLogValue(e));
                    changes.add(change);
                }
            }
        }

        for (PreferenceEligibility newEligibility: newEligibilities) {
            PreferenceEligibility e = getByAoId(oldEligibilities, newEligibility.getAoId());
            if (e == null) {
                Map<String, String> change = new HashMap<>();
                change.put(FIELD, auditLogKey(newEligibility));
                change.put(NEW_VALUE, auditLogValue(newEligibility));
                changes.add(change);
            }
        }
        return changes;
    }

    private static PreferenceEligibility getByAoId(List<PreferenceEligibility> eligibilities, String aoId) {
        for (PreferenceEligibility e: eligibilities) {
            if (e.getAoId().equals(aoId)) return e;
        }
        return null;
    }

    public static List<Map<String, String>> oldAndNewAdditinalInfoToListOfChanges(Map<String, String> oldAdditionalInfo, Map<String, String> newAdditionalInfo) {
        List<Map<String, String>> changes = new ArrayList<>();

        for(String key : oldAdditionalInfo.keySet()) {
            String oldValue = oldAdditionalInfo.get(key);
            String newValue = newAdditionalInfo.get(key);

            if(newValue == null) {
                Map<String, String> change = new HashMap<>();
                change.put(FIELD, key);
                change.put(OLD_VALUE, oldValue);
                changes.add(change);
            } else {
                if(!newValue.equals(oldValue)) {
                    Map<String, String> change = new HashMap<>();
                    change.put(FIELD, key);
                    change.put(OLD_VALUE, oldValue);
                    change.put(NEW_VALUE, newValue);
                    changes.add(change);
                }
            }
        }

        for(String key : newAdditionalInfo.keySet()) {
            if(!oldAdditionalInfo.containsKey(key)) {
                String newValue = newAdditionalInfo.get(key);
                Map<String, String> change = new HashMap<>();
                change.put(FIELD, key);
                change.put(NEW_VALUE, newValue);
                changes.add(change);
            }
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
