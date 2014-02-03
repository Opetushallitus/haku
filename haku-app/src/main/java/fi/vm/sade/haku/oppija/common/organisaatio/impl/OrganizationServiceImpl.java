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
package fi.vm.sade.haku.oppija.common.organisaatio.impl;

import com.google.common.collect.Lists;
import fi.vm.sade.generic.rest.CachingRestClient;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationRestDTO;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.TranslationsUtil;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Profile("default")
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceImpl.class);


    @Value("${web.url.cas}")
    private String casUrl;

    @Value("${cas.service.organisaatio-service}")
    private String targetService;

    @Value("${authentication.app.username.to.organisaatioservice}")
    private String clientAppUser;
    @Value("${authentication.app.password.to.organisaatioservice}")
    private String clientAppPass;

    private final OrganisaatioSearchService service;

    @Autowired
    public OrganizationServiceImpl(final OrganisaatioSearchService service) {
        this.service = service;
    }

    @Override
    public List<Organization> search(final OrganisaatioSearchCriteria searchCriteria) {

        LOG.debug("search organization kunta: {}, oidRestrictions: {}, loiType: {}, orgType: {}, q: {}, skipParents: {}",
                new String[]{
                    searchCriteria.getKunta(),
                    String.valueOf(searchCriteria.getOidRestrictionList().size()),
                    searchCriteria.getOppilaitosTyyppi(),
                    searchCriteria.getOrganisaatioTyyppi(),
                    searchCriteria.getSearchStr(),
                    String.valueOf(searchCriteria.getSkipParents())});

        final List<OrganisaatioPerustieto> result = service.searchBasicOrganisaatios(searchCriteria);

        LOG.debug("Criteria: {}, found {} organizations", searchCriteria, result.size());
        return Lists.newArrayList(Lists.transform(result, new OrganisaatioPerustietoToOrganizationFunction()));
    }

    @Override
    public List<String> findParentOids(final String organizationOid) {
        return service.findParentOids(organizationOid);
    }

    @Override
    public Organization findByOid(String oid) {
        Set<String> singleOid = Collections.singleton(oid);
        List<Organization> orgs = Lists.newArrayList(Lists.transform(service.findByOidSet(singleOid),
                new OrganisaatioPerustietoToOrganizationFunction()));
        if (orgs.size() == 1) {
            return orgs.get(0);
        } else if (orgs.size() > 1) {
            LOG.error("Got more than organizations for single oid: {}", oid);
            throw new RuntimeException("Got more than organizations for single oid");
        }
        return null;
    }

    @Override
    public List<Organization> findByOppilaitosnumero(List<String> oppilaitosnumeros) {
        CachingRestClient cachingRestClient = new CachingRestClient();
        List<Organization> orgs = new ArrayList<Organization>(oppilaitosnumeros.size());
        String baseUrl = targetService + "/rest/organisaatio/";
        try {
            for (String numero : oppilaitosnumeros) {
                OrganizationRestDTO orgDTO = cachingRestClient.get(baseUrl + numero, OrganizationRestDTO.class);
                Map<String, String> nameTranslations = TranslationsUtil.createTranslationsMap(orgDTO.getNimi());
                Organization org = new Organization(new I18nText(nameTranslations), orgDTO.getOid(),
                        orgDTO.getParentOid(), orgDTO.getTyypit(), orgDTO.getAlkuPvmAsDate(),
                        orgDTO.getLoppuPvmAsDate());
                orgs.add(org);
            }
            return orgs;
        } catch (IOException e) {
            LOG.error("Couldn't find organization for oppilaitosnumero", e);
        }
        return null;
    }

}
