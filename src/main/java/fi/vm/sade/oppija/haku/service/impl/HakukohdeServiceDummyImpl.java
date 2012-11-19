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

import fi.vm.sade.oppija.haku.domain.Hakukohde;
import fi.vm.sade.oppija.haku.domain.Organisaatio;
import fi.vm.sade.oppija.haku.service.HakukohdeService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of education service.
 *
 * @author Mikko Majapuro
 */
@Service("hakukohdeServiceDummyImpl")
public class HakukohdeServiceDummyImpl implements HakukohdeService {

    public static final int AMOUNT_OF_TEST_OPETUSPISTE = 1000;
    public static final int AMOUNT_OF_TEST_HAKUKOHDE = 5;
    private List<Organisaatio> institutes = new ArrayList<Organisaatio>();
    private Map<String, List<Hakukohde>> hakukohdeMap = new HashMap<String, List<Hakukohde>>();
    public static final int MAX_RESULTS = 10;

    public HakukohdeServiceDummyImpl() {
        // populate test data
        for (int i = 0; i < AMOUNT_OF_TEST_OPETUSPISTE; ++i) {
            Organisaatio op = new Organisaatio(String.valueOf(i), "Koulu" + i);
            institutes.add(op);
        }

        for (Organisaatio institute : institutes) {
            List<Hakukohde> hakukohdeList = new ArrayList<Hakukohde>();
            for (int i = 0; i < AMOUNT_OF_TEST_HAKUKOHDE; i++) {
                String id = institute.getId() + "_" + String.valueOf(i);
                hakukohdeList.add(new Hakukohde(id, "Hakukohde_" + id));
            }
            hakukohdeMap.put(institute.getId(), hakukohdeList);
        }
    }

    @Override
    public List<Organisaatio> searchOrganisaatio(final String hakuId, String term) {
        List<Organisaatio> result = new ArrayList<Organisaatio>();
        if (term != null && !term.trim().isEmpty()) {
            term = term.trim().toLowerCase(Locale.getDefault());
            for (Organisaatio o : institutes) {
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

    @Override
    public List<Hakukohde> searchHakukohde(final String hakuId, String organisaatioId) {
        return hakukohdeMap.get(organisaatioId);
    }
}
