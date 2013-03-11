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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import fi.vm.sade.oppija.common.organisaatio.Organization;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchCriteriaDTO;

/**
 * Create "mock" data from org service.
 */
public class CreateOrgMockData {

    public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/application-context.xml");
        OrganisaatioService service = (OrganisaatioService) context.getBean("organisaatioService");

        OrganisaatioSearchCriteriaDTO criteria = new OrganisaatioSearchCriteriaDTO();
        criteria.setMaxResults(1000);
        criteria.setSearchStr("espoo");
        List<OrganisaatioPerustietoType> result = service.searchBasicOrganisaatios(criteria);
        List<Organization> transformed = Lists.newArrayList(Iterables.transform(result,
                new OrganisaatioPerustietoTypeToOrganizationFunction()));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writer().writeValue(new File("/tmp/orgs.json"), transformed);
        context.close();
    }
}
