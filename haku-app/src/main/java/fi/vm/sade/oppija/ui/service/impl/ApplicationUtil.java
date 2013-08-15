package fi.vm.sade.oppija.ui.service.impl;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.hakemus.domain.ApplicationPhase;

public class ApplicationUtil {
    private ApplicationUtil() {
    }

    public static MapDifference<String, String> diffAnswers(final Application application, final ApplicationPhase applicationPhase) {
        return Maps.difference(application.getPhaseAnswers(applicationPhase.getPhaseId()), applicationPhase.getAnswers());
    }
}
