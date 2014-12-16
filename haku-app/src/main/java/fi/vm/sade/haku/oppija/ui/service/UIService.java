/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.oppija.ui.service;

import org.apache.http.HttpResponse;

import fi.vm.sade.haku.oppija.lomake.domain.ModelResponse;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

public interface UIService {

    ModelResponse getCompleteApplication(final String applicationSystemId, final String oid);

    ModelResponse getAllApplicationSystems(final String... includeFields);

    ModelResponse getPreview(final String applicationSystemId);

    ModelResponse getPhase(final String applicationSystemId, final String phaseId, final String lang);

    void storePrefilledAnswers(final String applicationSystemId, final Map<String, String> answers, String lang);

    Map<String, Object> getElementHelp(final String applicationSystemId, final String elementId, final Map<String, String> map);

    Map<String, Object> getAdditionalLanguageRow(final String applicationSystemId, final String gradeGridId);

    ModelResponse updateRules(final String applicationSystemId, final String phaseId, final String elementId, Map<String, String> currentAnswers);

    ModelResponse getPhaseElement(final String applicationSystemId, final String phaseId, final String elementId);

    ModelResponse savePhase(final String applicationSystemId, final String phaseId, Map<String, String> answers, String lang);

    ModelResponse submitApplication(final String applicationSystemId, String language);

    ModelResponse getApplication(final String applicationSystemId);

    HttpResponse getUriToPDF(String applicationSystemId, String oid);

    String ensureLanguage(HttpServletRequest request, String applicationSystemId);
}
