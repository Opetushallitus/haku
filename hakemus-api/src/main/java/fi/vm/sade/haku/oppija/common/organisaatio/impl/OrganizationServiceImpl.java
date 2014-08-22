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
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

    public OrganizationServiceImpl() {
        this.cache = new HashMap<String, SoftReference<Object>>();
    }

    @Override
    public List<Organization> search(final OrganisaatioSearchCriteria searchCriteria) throws UnsupportedEncodingException {

        String baseUrl = targetService + "/rest/organisaatio/hae";
        String params = buildParamString(searchCriteria);

        OrganisaatioHakutulos hakutulos = null;
        try {
            hakutulos = getCached(baseUrl + params, OrganisaatioHakutulos.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flattenAndTransformResultList(hakutulos);
    }

    private List<Organization> flattenAndTransformResultList(OrganisaatioHakutulos hakutulos) {
        List<Organization> orgs = new ArrayList<Organization>(hakutulos.getNumHits());
        OrganisaatioPerustietoToOrganizationFunction transformer = new OrganisaatioPerustietoToOrganizationFunction();
        for (OrganisaatioPerustieto perustieto : hakutulos.getOrganisaatiot()) {
            orgs = flattenChildren(orgs, transformer, perustieto);
        }
        return orgs;
    }

    private List<Organization> flattenChildren(List<Organization> orgs,
                                               OrganisaatioPerustietoToOrganizationFunction transformer,
                                               OrganisaatioPerustieto perustieto) {
        orgs.add(transformer.apply(perustieto));
        for (OrganisaatioPerustieto childOrg : perustieto.getChildren()) {
            orgs = flattenChildren(orgs, transformer, childOrg);
        }
        return orgs;
    }

    private String buildParamString(OrganisaatioSearchCriteria criteria) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder("?");

        if (criteria.getOppilaitosTyyppi() != null && !criteria.getOppilaitosTyyppi().isEmpty()) {
            builder.append("oppilaitosTyyppi=")
                    .append(URLEncoder.encode(criteria.getOppilaitosTyyppi().toArray()[0].toString(), "UTF-8"))
                    .append("&");
        }
        if (isNotBlank(criteria.getOrganisaatioTyyppi())) {
            builder.append("organisaatioTyyppi=")
                    .append(URLEncoder.encode(criteria.getOrganisaatioTyyppi(), "UTF-8"))
                    .append("&");
        }
        if (isNotBlank(criteria.getSearchStr())) {
            builder.append("searchStr=")
                    .append(URLEncoder.encode(criteria.getSearchStr(), "UTF-8"))
                    .append("&");
        }
        builder.append("skipParents=").append(String.valueOf(criteria.getSkipParents())).append("&")
                .append("suunnitellut=").append(String.valueOf(criteria.getSuunnitellut())).append("&")
                .append("vainLakkautetut=").append(String.valueOf(criteria.getVainLakkautetut())).append("&")
                .append("vainAktiiviset=").append(String.valueOf(criteria.getVainAktiiviset()));
        return builder.toString();
    }

    @Override
    public List<String> findParentOids(final String organizationOid) throws IOException {
        String url = targetService + "/rest/organisaatio/" + organizationOid + "/parentoids";
        CachingRestClient client = getCachingRestClient();
        String parents = client.getAsString(url);
        return Lists.newArrayList(parents.split("/"));
    }

    @Override
    public Organization findByOid(String oid) throws IOException {
        String url = targetService + "/rest/organisaatio/" + oid;
        OrganizationRestDTO organisaatioRDTO = getCached(url, OrganizationRestDTO.class);
        return new Organization(organisaatioRDTO);
    }

    @Override
    public List<Organization> findByOppilaitosnumero(List<String> oppilaitosnumeros) {
        List<Organization> orgs = new ArrayList<Organization>(oppilaitosnumeros.size());
        int i = 1;
        try {
            LOG.debug("Getting {} oppilaitosnumeros", oppilaitosnumeros.size());
            for (String numero : oppilaitosnumeros) {
                LOG.debug("Getting oppilaitosnumero {} ({} / {})", numero, i++, oppilaitosnumeros.size());
                orgs.add(findByOid(numero));
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

    public static void setCachingRestClient(CachingRestClient client) {
        cachingRestClient = client;
    }

    private synchronized CachingRestClient getCachingRestClient() {
        if (cachingRestClient == null) {
            cachingRestClient = new CachingRestClient();
        }
        return cachingRestClient;
    }

}
