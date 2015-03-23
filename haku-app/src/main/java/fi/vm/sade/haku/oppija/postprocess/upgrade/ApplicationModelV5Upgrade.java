package fi.vm.sade.haku.oppija.postprocess.upgrade;


import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider;
import fi.vm.sade.haku.oppija.hakemus.aspect.LoggerAspect;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationNote;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.KoodistoService;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.vm.sade.haku.oppija.hakemus.aspect.ApplicationDiffUtil.addHistoryBasedOnChangedAnswers;

public final class ApplicationModelV5Upgrade implements ModelUpgrade<Application> {

    private static final int baseVersion = 4;
    private static final int targetVersion = 5;

    private boolean enableUpgradeV5;

    static final String APPLICABLE_APPLICATIONSYSTEM_ID = "1.2.246.562.29.95390561488";
    private final List<String> ammattitutkintonimikkeet;
    private final List<String> ammattioppilaitokset;

    static final String YO_AMMATILLINEN_TUTKINTO = "pohjakoulutus_yo_ammatillinen_nimike";
    static final String YO_AMMATILLINEN_OPPILAITOS = "pohjakoulutus_yo_ammatillinen_oppilaitos";

    static final String AM_TUTKINTO_PREFIX = "pohjakoulutus_am_nimike";
    static final String AM_OPPILAITOS_PREFIX = "pohjakoulutus_am_oppilaitos";
    static final String MUU_POSTFIX = "_muu";

    private final LoggerAspect loggerAspect;

    public ApplicationModelV5Upgrade(final KoodistoService koodistoService, final LoggerAspect loggerAspect, final boolean enabled) {
        this.enableUpgradeV5 = enabled;
        this.loggerAspect = loggerAspect;
        final List koodistoTutkinnot = Lists.newArrayList(Iterables.transform(koodistoService.getAmmattitutkinnot(), new Function<Option, String>() {
            @Override
            public String apply(Option input) {
                return input.getValue();
            }
        }));
        koodistoTutkinnot.add(OppijaConstants.TUTKINTO_MUU);
        ammattitutkintonimikkeet = ImmutableList.copyOf(koodistoTutkinnot);

        final List koodistoOppilaitokset = Lists.newArrayList(Iterables.transform(koodistoService.getAmmattioppilaitosKoulukoodit(), new Function<Option, String>() {
            @Override
            public String apply(Option input) {
                return input.getValue();
            }
        }));
        koodistoOppilaitokset.add(OppijaConstants.OPPILAITOS_TUNTEMATON);
        ammattioppilaitokset = ImmutableList.copyOf(koodistoOppilaitokset);
    }

    @Override
    public boolean enabled() {
        return enableUpgradeV5;
    }

    @Override
    public int getBaseVersion() {
        return baseVersion;
    }

    @Override
    public int getTargetVersion() {
        return targetVersion;
    }

    private Map<String, String> processAnswers(final Map<String, String> originalAnswers) {
        final HashMap<String, String> changes = new HashMap<>();
        for (Map.Entry<String, String> answer : originalAnswers.entrySet()) {
            String key = answer.getKey();

            if (key.equals(YO_AMMATILLINEN_TUTKINTO)) {
                if (!ammattitutkintonimikkeet.contains(answer.getValue()))
                    addChange(changes, answer, OppijaConstants.TUTKINTO_MUU, null);
            } else if (key.equals(YO_AMMATILLINEN_OPPILAITOS)) {
                if (!ammattioppilaitokset.contains(answer.getValue()))
                    addChange(changes, answer, OppijaConstants.OPPILAITOS_TUNTEMATON, null);
            } else if (key.startsWith(AM_TUTKINTO_PREFIX)) {
                if (!ammattitutkintonimikkeet.contains(answer.getValue()))
                    addChange(changes, answer, OppijaConstants.TUTKINTO_MUU, AM_TUTKINTO_PREFIX);
            } else if (key.startsWith(AM_OPPILAITOS_PREFIX)) {
                if (!ammattioppilaitokset.contains(answer.getValue()))
                    addChange(changes, answer, OppijaConstants.OPPILAITOS_TUNTEMATON, AM_OPPILAITOS_PREFIX);
            }
        }

        if (changes.isEmpty())
            return originalAnswers;
        final HashMap<String, String> newAnswers = new HashMap<>(originalAnswers);
        newAnswers.putAll(changes);
        return newAnswers;
    }

    private void addChange(final Map<String, String> changes, final Map.Entry<String, String> answer, final String codeValue, final String prefix) {
        final String key = answer.getKey();

        changes.put(key, codeValue);
        if (null == prefix || prefix.length() == key.length()) {
            changes.put(key + MUU_POSTFIX, answer.getValue());
        }
        else  {
            final String index = key.substring(prefix.length());
            changes.put(prefix + MUU_POSTFIX +index, answer.getValue());
        }
    }


    @Override
    public UpgradeResult<Application> processUpgrade(final Application application) {
        if (!APPLICABLE_APPLICATIONSYSTEM_ID.equals(application.getApplicationSystemId()))
            return new UpgradeResult<>(application, false);

        final Map<String, String> oldEducationPhaseAnswers = application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION);
        final Map<String, String> newEducationPhaseAnswers = processAnswers(oldEducationPhaseAnswers);

        if (oldEducationPhaseAnswers == newEducationPhaseAnswers)
            return new UpgradeResult<>(application, false);

        return logAndReturnModified(application, newEducationPhaseAnswers);
    }

    private UpgradeResult<Application> logAndReturnModified(Application application, Map<String, String> newEducationPhaseAnswers) {
        final Application original = application.clone();
        application.addVaiheenVastaukset(OppijaConstants.PHASE_EDUCATION, newEducationPhaseAnswers);
        application.setModelVersion(targetVersion);
        application = logOperation(original, application, "Automaattikäsittely: ammatilliset pohjakoulutukset sidottu koodeihin ja vapaa selite siirretty.");
        loggerAspect.logUpdateApplication(original,
                new ApplicationPhase(
                        original.getApplicationSystemId(),
                        OppijaConstants.PHASE_EDUCATION,
                        application.getPhaseAnswers(OppijaConstants.PHASE_EDUCATION)));
        return new UpgradeResult<>(application, true);
    }

    private static Application logOperation(Application original, Application modified, String note) {
        final String SYSTEM_USER = "järjestelmä";
        addHistoryBasedOnChangedAnswers(modified, original, SYSTEM_USER, note);
        modified.addNote(new ApplicationNote(note, new Date(), SYSTEM_USER));
        return modified;
    }
}