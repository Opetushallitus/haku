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
package fi.vm.sade.oppija.common.organisaatio.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.oppija.common.organisaatio.Organization;
import fi.vm.sade.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("default")
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    public static final int MAX_RESULTS = 10000;
    private final OrganisaatioSearchService service;

    @Autowired
    public OrganizationServiceImpl(final OrganisaatioSearchService service) {
        this.service = service;
    }

    @Override
    public List<Organization> search(final OrganisaatioSearchCriteria searchCriteria) {

//        final OrganisaatioSearchCriteria criteria = new OrganisaatioSearchCriteria();
//        criteria.setMaxResults(MAX_RESULTS);
//        criteria.setSearchStr(searchCriteria.getSearchString());
//        criteria.setOrganisaatioTyyppi(searchCriteria.getOrganizationType());
//        criteria.setSuunnitellut(searchCriteria.isIncludePlanned());
//        criteria.setLakkautetut(searchCriteria.isIncludePassive());
//        criteria.setOppilaitosTyyppi(searchCriteria.getLearningInstitutionType());
//        searchCriteria.setMaxResults(MAX_RESULTS);
        final List<OrganisaatioPerustieto> result = service.searchBasicOrganisaatios(searchCriteria);

        LOG.debug("Criteria: {}, found {} organizations", searchCriteria, result.size());
        return Lists.newArrayList(Lists.transform(result, new OrganisaatioPerustietoToOrganizationFunction()));
    }

    @Override
    public List<String> findParentOids(final String organizationOid) {
        return service.findParentOids(organizationOid);
    }

}
