/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.haku.oppija.hakemus.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class ApplicationPhase {

    private final String applicationSystemId;
    private final String phaseId;
    private final Map<String, String> answers = new HashMap<String, String>();

    public ApplicationPhase(final String applicationSystemId, final String phaseId, final Map<String, String> answers) {
        Preconditions.checkNotNull(applicationSystemId, "applicationSystemId is null");
        Preconditions.checkNotNull(phaseId, "phaseId is null");
        Preconditions.checkNotNull(answers, "answers is null");
        this.applicationSystemId = applicationSystemId;
        this.phaseId = phaseId;
        this.answers.putAll(answers);
    }

    public String getApplicationSystemId() {
        return applicationSystemId;
    }

    public String getPhaseId() {
        return phaseId;
    }

    public Map<String, String> getAnswers() {
        return this.answers;
    }
}
