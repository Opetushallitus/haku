package fi.vm.sade.haku.oppija.hakemus.aspect;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.hakemus.domain.ApplicationPhase;

public final class ApplicationDiffUtil {
    private ApplicationDiffUtil() {
    }

    public static MapDifference<String, String> diffAnswers(final Application application, final ApplicationPhase applicationPhase) {
        return Maps.difference(application.getPhaseAnswers(applicationPhase.getPhaseId()), applicationPhase.getAnswers());
    }
}
