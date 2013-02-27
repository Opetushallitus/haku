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
package fi.vm.sade.oppija.common.valintaperusteet.impl;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;

import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.common.valintaperusteet.InputParameter;

//XXX this is fragile because the representation might change any time
public class MapToAdditionalQuestionsFunction implements
        Function<Map<String, Map<String, Map<String, String>>>, AdditionalQuestions> {

    public AdditionalQuestions apply(Map<String, Map<String, Map<String, String>>> input) {
        final AdditionalQuestions aq = new AdditionalQuestions();

        for (Entry<String, Map<String, Map<String, String>>> oid : input.entrySet()) {
            String oidKey = oid.getKey();
            final Map<String, Map<String, String>> value = oid.getValue();
            for (String phase : value.keySet()) {
                for (Map.Entry<String, String> entry : value.get(phase).entrySet()) {
                    final String key = entry.getKey();
                    final String type = entry.getValue();
                    InputParameter param = new InputParameter(key, type, phase);
                    aq.addParameter(oidKey, param);
                }
            }
            
        }
        return aq;
    }

}
