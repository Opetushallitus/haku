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

package fi.vm.sade.oppija.haku.service.impl;

import fi.vm.sade.oppija.haku.domain.Opetuspiste;
import fi.vm.sade.oppija.haku.service.EducationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of education service.
 *
 * @author Mikko Majapuro
 */
@Service("educationService")
public class EducationServiceImpl implements EducationService {

    public static final int AMOUNT_OF_TEST_OPETUSPISTE = 1000;
    private List<Opetuspiste> institutes = new ArrayList<Opetuspiste>();
    public static final int MAX_RESULTS = 10;

    public EducationServiceImpl() {
        // populate test data
        for (int i = 0; i < AMOUNT_OF_TEST_OPETUSPISTE; ++i) {
            Opetuspiste op = new Opetuspiste(String.valueOf(i), "Koulu" + i);
            institutes.add(op);
        }
    }

    @Override
    public List<Opetuspiste> searchEducationInstitutes(String term) {
        List<Opetuspiste> result = new ArrayList<Opetuspiste>();
        if (term != null && !term.trim().isEmpty()) {
            term = term.trim().toLowerCase(Locale.getDefault());
            for (Opetuspiste o : institutes) {
                if (o.getKey().startsWith(term)) {
                    result.add(o);
                    if (result.size() >= MAX_RESULTS) {
                        break;
                    }
                }
            }
        }
        return result;
    }
}
