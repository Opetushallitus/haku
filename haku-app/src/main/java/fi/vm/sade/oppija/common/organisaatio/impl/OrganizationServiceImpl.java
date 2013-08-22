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
import fi.vm.sade.oppija.common.organisaatio.SearchCriteria;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchCriteriaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("default")
public class OrganizationServiceImpl implements OrganizationService {

    public static final int MAX_RESULTS = 10000;
    private final OrganisaatioService service;

    @Autowired
    public OrganizationServiceImpl(final OrganisaatioService service) {
        this.service = service;
    }

    @Override
    public List<Organization> search(final SearchCriteria criteria) {

        final OrganisaatioSearchCriteriaDTO criteriaDTO = new OrganisaatioSearchCriteriaDTO();
        criteriaDTO.setMaxResults(MAX_RESULTS);
        criteriaDTO.setSearchStr(criteria.getSearchString());
        criteriaDTO.setOrganisaatioTyyppi(criteria.getOrganizationType());
        criteriaDTO.setSuunnitellut(criteria.isIncludePlanned());
        criteriaDTO.setLakkautetut(criteria.isIncludePassive());
        criteriaDTO.setOppilaitosTyyppi(criteria.getLearningInstitutionType());
        final List<OrganisaatioDTO> result = service.searchOrganisaatios(criteriaDTO);
        return Lists.newArrayList(Lists.transform(result, new OrganisaatioDTOToOrganizationFunction()));
    }

    @Override
    public List<String> findParentOids(final String organizationOid) {
        List<OrganisaatioDTO> parents = service.findParentsTo(organizationOid);
        List<String> parentOids = new ArrayList<String>(parents.size());
        for (OrganisaatioDTO org : parents) {
            parentOids.add(org.getOid());
        }
        return parentOids;
    }

}
