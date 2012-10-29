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
import fi.vm.sade.oppija.tarjonta.domain.SearchResult;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        parameters.put("LOPInstitutionInfoName", createParameter(term + "*"));
        parameters.put("tmpHakuId", createParameter(hakuId));
        SearchResult search = service.search(parameters);
        List<Organisaatio> organisaatios = new ArrayList<Organisaatio>(search.getSize());
        List<Map<String, Object>> items = search.getItems();
        String lopInstitutionInfoName;
        for (Map<String, Object> item : items) {
            lopInstitutionInfoName = (String) item.get("LOPInstitutionInfoName");
            organisaatios.add(new Organisaatio(lopInstitutionInfoName, lopInstitutionInfoName));
        }
        return organisaatios;
    }

    @Override
    public List<Hakukohde> searchHakukohde(final String hakuId, final String organisaatioId) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(2);
        parameters.put("tmpHakuId", createParameter(hakuId));
        parameters.put("LOPInstitutionInfoName", createParameter(organisaatioId));
        SearchResult search = service.search(parameters);

        List<Hakukohde> hakukohteet = new ArrayList<Hakukohde>(search.getSize());
        List<Map<String, Object>> items = search.getItems();
        String lopInstitutionInfoName;
        for (Map<String, Object> item : items) {
            lopInstitutionInfoName = (String) item.get("LOPInstitutionInfoName");
            hakukohteet.add(new Hakukohde(lopInstitutionInfoName, lopInstitutionInfoName));
        }
        return hakukohteet;
    }

    private List<String> createParameter(String value) {
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(value);
        return parameters;

    }
}
