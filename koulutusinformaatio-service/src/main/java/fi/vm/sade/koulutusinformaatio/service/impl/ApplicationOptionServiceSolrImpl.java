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

package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.Organization;
import fi.vm.sade.koulutusinformaatio.domain.SearchResult;
import fi.vm.sade.koulutusinformaatio.service.ApplicationOptionService;
import fi.vm.sade.koulutusinformaatio.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

//@Service("applicationOptionServiceSolrImpl")
public class ApplicationOptionServiceSolrImpl implements ApplicationOptionService {

    private final SearchService service;

    @Autowired
    public ApplicationOptionServiceSolrImpl(SearchService service) {
        this.service = service;
    }

    @Override
    public List<Organization> searchOrganisaatio(final String hakuId, final String term) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(2);
        Set<Organization> organizations = new HashSet<Organization>();
        String startswith = term.trim();
        if (!startswith.isEmpty()) {
            parameters.put("LOPInstitutionInfoName", createParameter(term + "*"));
            parameters.put("ASName", createParameter(hakuId));
            SearchResult search = service.search(parameters.entrySet());
            List<Map<String, Object>> items = search.getItems();
            for (Map<String, Object> item : items) {
                organizations.add(new Organization((String) item.get("LOPOid"), (String) item.get("LOPInstitutionInfoName")));
            }
        }
        return new ArrayList<Organization>(organizations);
    }

    @Override
    public List<ApplicationOption> searchHakukohde(final String hakuId, final String organisaatioId) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>(2);
        parameters.put("ASName", createParameter(hakuId));
        parameters.put("LOPOid", createParameter(organisaatioId));
        SearchResult search = service.search(parameters.entrySet());

        List<ApplicationOption> hakukohteet = new ArrayList<ApplicationOption>(search.getSize());
        List<Map<String, Object>> items = search.getItems();
        for (Map<String, Object> item : items) {
            hakukohteet.add(new ApplicationOption((String) item.get("AOId"), (String) item.get("AOTitle"), (String) item.get("AOEducationDegree")));
        }
        return hakukohteet;
    }

    private List<String> createParameter(String value) {
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(value);
        return parameters;

    }
}
