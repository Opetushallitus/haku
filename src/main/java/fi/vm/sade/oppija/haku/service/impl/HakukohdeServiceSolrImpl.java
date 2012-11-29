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

import fi.vm.sade.oppija.haku.domain.ApplicationOption;
import fi.vm.sade.oppija.haku.domain.Organisaatio;
import fi.vm.sade.oppija.haku.service.HakukohdeService;
import fi.vm.sade.oppija.tarjonta.domain.SearchResult;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

@Service("HakukohdeServiceSolrImpl")
public class HakukohdeServiceSolrImpl implements HakukohdeService {

    private final SearchService service;

    @Autowired
    public HakukohdeServiceSolrImpl(SearchService service) {
        this.service = service;
    }

    @Override
    public List<Organisaatio> searchOrganisaatio(final String hakuId, final String term) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(1);
        Set<Organisaatio> organisaatios = new HashSet<Organisaatio>();
        String startswith = term.trim();
        if (!startswith.isEmpty()) {
            parameters.put("LOPInstitutionInfoName", createParameter(term + "*"));
            parameters.put("ASName", createParameter(hakuId));
            SearchResult search = service.search(parameters);
            List<Map<String, Object>> items = search.getItems();
            for (Map<String, Object> item : items) {
                organisaatios.add(new Organisaatio((String) item.get("LOPId"), (String) item.get("LOPInstitutionInfoName")));
            }
        }
        return new ArrayList<Organisaatio>(organisaatios);
    }

    @Override
    public List<ApplicationOption> searchHakukohde(final String hakuId, final String organisaatioId) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(2);
        parameters.put("ASName", createParameter(hakuId));
        parameters.put("LOPId", createParameter(organisaatioId));
        SearchResult search = service.search(parameters);

        List<ApplicationOption> hakukohteet = new ArrayList<ApplicationOption>(search.getSize());
        List<Map<String, Object>> items = search.getItems();
        for (Map<String, Object> item : items) {
            hakukohteet.add(new ApplicationOption((String) item.get("LOSId"), (String) item.get("AOTitle")));
        }
        return hakukohteet;
    }

    private List<String> createParameter(String value) {
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(value);
        return parameters;

    }
}
