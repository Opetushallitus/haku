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

package fi.vm.sade.oppija.hakemus.domain;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.domain.FormId;

import java.util.HashMap;
import java.util.Map;

public class ApplicationPhase {

    private final FormId formId;
    private final String phaseId;
    private final Map<String, String> answers = new HashMap<String, String>();

    public ApplicationPhase(final FormId formId, final String phaseId, final Map<String, String> answers) {
        this.formId = formId;
        this.phaseId = phaseId;
        this.answers.putAll(answers);
    }

    public FormId getFormId() {
        return formId;
    }

    public String getPhaseId() {
        return phaseId;
    }

    public Map<String, String> getAnswers() {
        return ImmutableMap.copyOf(this.answers);
    }
}
