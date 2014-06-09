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
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.organisaatio.service.search.SearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.*;

@Service
@Profile(value = {"default", "devluokka"})
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private static CachingRestClient cachingRestClient;
    private static Map<String, SoftReference<Object>> cache;
    private static final String ROOT_ORGANIZATION_OPH = "1.2.246.562.10.00000000001";

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
        this.cache = new HashMap<String, SoftReference<Object>>();
    }

    @Override
    public List<Organization> search(final SearchCriteria searchCriteria) {

        LOG.debug("search organization kunta: {}, oidRestrictions: {}, loiType: {}, orgType: {}, q: {}, skipParents: {}",
                searchCriteria.getKunta(),
                String.valueOf(searchCriteria.getOidRestrictionList().size()),
                searchCriteria.getOppilaitosTyyppi(),
                searchCriteria.getOrganisaatioTyyppi(),
                searchCriteria.getSearchStr(),
                String.valueOf(searchCriteria.getSkipParents()));

        final List<OrganisaatioPerustieto> result = service.searchBasicOrganisaatios(searchCriteria);

        LOG.debug("Criteria: {}, found {} organizations", searchCriteria, result.size());
        return Lists.newArrayList(Lists.transform(result, new OrganisaatioPerustietoToOrganizationFunction()));
    }

    @Override
    public List<String> findParentOids(final String organizationOid) {
        // Fix some service curiosities
        List<String> parentOids = service.findParentOids(organizationOid);
        if (! parentOids.contains(organizationOid)){
            parentOids.add(organizationOid);
        }
        if (! parentOids.contains(ROOT_ORGANIZATION_OPH)){
            parentOids.add(ROOT_ORGANIZATION_OPH);
        }
        return parentOids;
    }

    @Override
    public Organization findByOid(String oid) {
        Set<String> singleOid = Collections.singleton(oid);
        List<Organization> orgs = Lists.newArrayList(Lists.transform(service.findByOidSet(singleOid),
                new OrganisaatioPerustietoToOrganizationFunction()));
        if (orgs.size() == 1) {
            return orgs.get(0);
        } else if (orgs.size() > 1) {
            LOG.error("Got more than one organizations for single oid: {}", oid);
            throw new RuntimeException("Got more one than organizations for single oid");
        }
        return null;
    }

    @Override
    public List<Organization> findByOppilaitosnumero(List<String> oppilaitosnumeros) {
        List<Organization> orgs = new ArrayList<Organization>(oppilaitosnumeros.size());
        String baseUrl = targetService + "/rest/organisaatio/";
        int i = 1;
        try {
            LOG.debug("Getting {} oppilaitosnumeros", oppilaitosnumeros.size());
            for (String numero : oppilaitosnumeros) {
                LOG.debug("Getting oppilaitosnumero {} ({} / {})", numero, i++, oppilaitosnumeros.size());
                OrganizationRestDTO orgDTO = getCached(baseUrl + numero, OrganizationRestDTO.class);
                Map<String, String> nameTranslations = TranslationsUtil.createTranslationsMap(orgDTO.getNimi());
                Organization org = new Organization(new I18nText(nameTranslations), orgDTO.getOid(),
                        orgDTO.getParentOid(), orgDTO.getTyypit(), orgDTO.getAlkuPvmAsDate(),
                        orgDTO.getLoppuPvmAsDate());
                orgs.add(org);
            }
            LOG.debug("Got numbers");
            return orgs;
        } catch (IOException e) {
            LOG.error("Couldn't find organization for oppilaitosnumero", e);
        }
        return null;
    }

    private <T> T getCached(String url, Class<? extends T> resultType) throws IOException {
        if (cache.containsKey(url)) {
            LOG.debug("Hit cache, url: {}", url);
            Object result = cache.get(url).get();

            if (null != result && resultType.isAssignableFrom(result.getClass())) {
                return (T) result;
            }
            LOG.debug("Cache reference for key {} is stale or unassignable. Result object was of type {} expected {}", url, null == result ? null : result.getClass(), resultType.getClass());
        }
        CachingRestClient client = getCachingRestClient();
        T result = client.get(url, resultType);
        cache.put(url, new SoftReference<Object>(result));
        return result;

    }

    private synchronized CachingRestClient getCachingRestClient() {
        if (cachingRestClient == null) {
            cachingRestClient = new CachingRestClient();
        }
        return cachingRestClient;
    }

}
