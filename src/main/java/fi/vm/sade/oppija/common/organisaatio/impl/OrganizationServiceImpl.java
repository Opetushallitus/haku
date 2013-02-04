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

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import fi.vm.sade.oppija.common.organisaatio.Organization;
import fi.vm.sade.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.oppija.common.organisaatio.SearchCriteria;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchCriteriaDTO;

public class OrganizationServiceImpl implements OrganizationService {

    //TODO inject 
    private OrganisaatioService service;

    @Override
    public List<Organization> search(SearchCriteria criteria) throws IOException {

        OrganisaatioSearchCriteriaDTO criteriaDTO = new OrganisaatioSearchCriteriaDTO();
        if (criteria.getSearchString() != null) {
            criteriaDTO.setSearchStr(criteria.getSearchString());
        }

        if (criteria.getType() != null) {
            criteriaDTO.setOrganisaatioTyyppi(criteria.getType().toString());
        }

        List<OrganisaatioPerustietoType> result = service.searchBasicOrganisaatios(criteriaDTO);
        return Lists.newArrayList(Lists.transform(result, new OrganisaatioPerustietoTypeToOrganizationFunction()));
    }

}
