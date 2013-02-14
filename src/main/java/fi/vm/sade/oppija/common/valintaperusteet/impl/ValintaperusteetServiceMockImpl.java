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

import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.google.common.collect.LinkedListMultimap;

import fi.vm.sade.oppija.common.valintaperusteet.AdditionalQuestions;
import fi.vm.sade.oppija.common.valintaperusteet.InputParameter;
import fi.vm.sade.oppija.common.valintaperusteet.ValintaperusteetService;

@Service
@Profile("default")
public class ValintaperusteetServiceMockImpl implements ValintaperusteetService {

    private final LinkedListMultimap<String, InputParameter> questions = LinkedListMultimap.create();

    public ValintaperusteetServiceMockImpl() {
        //TODO add reasonable mock data
        addQ("1234567", "1", "aidinkieli_yo", "DESIMAALILUKU");
        addQ("1234567", "2", "matematiikka_yo", "DESIMAALILUKU");
    }

    private void addQ(String oid, String phase, String key, String type) {
        final InputParameter param = new InputParameter(key, type, phase);
        questions.put(oid, param);
    }

    @Override
    public AdditionalQuestions retrieveAdditionalQuestions(List<String> oids) throws IOException {
        AdditionalQuestions aq = new AdditionalQuestions();
        for (String oid : oids) {
            for (InputParameter param : questions.get(oid)) {
                aq.addParameter(oid, param);
            }
        }
        return aq;
    }
}