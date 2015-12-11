package fi.vm.sade.haku.oppija.postprocess.upgrade;


import fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.hakemus.domain.Change;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil.addHistoryBasedOnChangedAnswers;

public final class ApplicationModelV6Upgrade implements ModelUpgrade<Application> {

    private static final int baseVersion = 5;
    private static final int targetVersion = 6;

    private static final String SYSTEM_USER = "järjestelmä";

    static final String APPLICABLE_APPLICATIONSYSTEM_ID = "1.2.246.562.29.95390561488";

    static final String AM_TUTKINTO_PREFIX = "pohjakoulutus_am_nimike";
    static final String AM_OPPILAITOS_PREFIX = "pohjakoulutus_am_oppilaitos";
    static final String MUU_POSTFIX = "_muu";

    private final LoggerAspect loggerAspect;
    private final Comparator<Change> historyComparator;

    public ApplicationModelV6Upgrade(final LoggerAspect loggerAspect) {
        this.loggerAspect = loggerAspect;
        this.historyComparator = new Comparator<Change>() {
            @Override
            public int compare(Change c1, Change c2) {
                if (c1 == c2)
                    return 0;
                else if (c1.getModified().before(c2.getModified()))
                    return -1;
                return 1;
            }
        };
    }

    @Override
    public int getBaseVersion() {
        return baseVersion;
    }

    @Override
    public int getTargetVersion() {
        return targetVersion;
    }

    private Map<String, String> processAnswers(final Map<String, String> originalAnswers, List<Change> applicationHistory) {
        final HashMap<String, String> newAnswers = new HashMap<>();
        final TreeSet<Change> history = new TreeSet<>(historyComparator);
        for (Map.Entry<String, String> answer : originalAnswers.entrySet()) {
            final String key = answer.getKey();

            if (key.startsWith(AM_TUTKINTO_PREFIX) && key.contains(MUU_POSTFIX)) {
                if (requiresFix(key, answer, OppijaConstants.TUTKINTO_MUU)){
                    if (newAnswers.isEmpty()) {
                        newAnswers.putAll(originalAnswers);
                        history.addAll(applicationHistory);
                    }
                    applyFix(answer, newAnswers, OppijaConstants.TUTKINTO_MUU, history);
                }
            } else if (key.startsWith(AM_OPPILAITOS_PREFIX) && key.contains(MUU_POSTFIX)) {
                if (requiresFix(key, answer, OppijaConstants.OPPILAITOS_TUNTEMATON)){
                    if (newAnswers.isEmpty()) {
                        newAnswers.putAll(originalAnswers);
                        history.addAll(applicationHistory);
                    }
                    applyFix(answer, newAnswers, OppijaConstants.OPPILAITOS_TUNTEMATON, history);
                }
            }
        }

        if (newAnswers.isEmpty())
            return originalAnswers;
        return newAnswers;
    }

    private boolean requiresFix(final String key, final Map.Entry<String, String> answer, final String codeValue){
        return key.replaceFirst(MUU_POSTFIX, StringUtils.EMPTY).contains(MUU_POSTFIX) || codeValue.equals(answer.getValue());
    }

    private void applyFix(final Map.Entry<String, String> answer, final Map newAnswers, final String codeValue, TreeSet<Change> applicationHistory){
        final String key = answer.getKey();
        if (key.replaceFirst(MUU_POSTFIX, StringUtils.EMPTY).contains(MUU_POSTFIX)) {
            newAnswers.remove(key);
        }
        else if (codeValue.equals(answer.getValue())){
            newAnswers.put(key, repairFromHistory(key, applicationHistory, codeValue));
        }
    }

    private String repairFromHistory(final String key, final TreeSet<Change> history, final String codeValue){
        for (Change historyItem : history){
            if (SYSTEM_USER.equals(historyItem.getModifier())){
                for (Map<String, String> change: historyItem.getChanges()){
                    if (key.equals(change.get(ApplicationDiffUtil.FIELD))&& codeValue.equals(change.get(ApplicationDiffUtil.NEW_VALUE)))
                        return change.get(ApplicationDiffUtil.OLD_VALUE);
                }
            }
        }
        throw new RuntimeException("ApplicationModelV6Upgrade: Repair failed for " + key);
    }

    @Override
    public UpgradeResult<Application> processUpgrade(final Application application) {
        if (!APPLICABLE_APPLICATIONSYSTEM_ID.equals(application.getApplicationSystemId()))
            return new UpgradeResult<>(application, false);

        final Map<String, String> oldEducationPhaseAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
        final Map<String, String> newEducationPhaseAnswers = processAnswers(oldEducationPhaseAnswers, application.getHistory());

        if (oldEducationPhaseAnswers == newEducationPhaseAnswers)
            return new UpgradeResult<>(application, false);

        return logAndReturnModified(application, newEducationPhaseAnswers);
    }

    private UpgradeResult<Application> logAndReturnModified(Application application, Map<String, String> newEducationPhaseAnswers) {
        final Application original = application.clone();
        application.setVaiheenVastauksetAndSetPhaseId(OppijaConstants.PHASE_EDUCATION, newEducationPhaseAnswers);
        application.setModelVersion(targetVersion);
        application = logOperation(original, application, "Automaattikäsittely: ammatilliset pohjakoulutukset korjaus.");
        loggerAspect.logUpdateApplication(original,
                new ApplicationPhase(
                        original.getApplicationSystemId(),
                        OppijaConstants.PHASE_EDUCATION,
                        application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION)));
        return new UpgradeResult<>(application, true);
    }

    private static Application logOperation(Application original, Application modified, String note) {
        addHistoryBasedOnChangedAnswers(modified, original, SYSTEM_USER, note);
        modified.addNote(new ApplicationNote(note, new Date(), SYSTEM_USER));
        return modified;
    }
}