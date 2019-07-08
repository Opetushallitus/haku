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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import fi.vm.sade.haku.oppija.common.organisaatio.*;
import fi.vm.sade.haku.oppija.lomake.exception.ResourceNotFoundException;
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import fi.vm.sade.properties.OphProperties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Profile(value = {"default", "devluokka", "vagrant"})
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private static final String ROOT_ORGANIZATION_OPH = "1.2.246.562.10.00000000001";
    private static final String ORGANIZATION_PREFIX = "1.2.246.562.10";
    private static final Pattern SUFFIX_PATTERN = Pattern.compile("^[0-9]{11}$");
    private static final String callerId = "1.2.246.562.10.00000000001.haku.hakemus-api";

    private final Map<String, SoftReference<Object>> cache;
    private HttpClient httpClient;
    private Gson gson;

    private OphProperties urlConfiguration;

    @Autowired
    public OrganizationServiceImpl(OphProperties urlConfiguration) {
        this.urlConfiguration = urlConfiguration;
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new TimestampDateAdapter());
        builder.registerTypeAdapter(OrganizationGroupListRestDTO.class, new OrganizationGroupListRestDTOAdapter());
        this.cache = new HashMap<>();
        this.gson = builder.create();
        this.httpClient = HttpClientBuilder.create()
                .disableAuthCaching()
                .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(120 * 1000).setSocketTimeout(60 * 1000).build())
                .setConnectionReuseStrategy(new NoConnectionReuseStrategy())
                .build();
    }

    @Override
    public List<Organization> search(final OrganisaatioSearchCriteria searchCriteria) throws UnsupportedEncodingException {
        OrganisaatioHakutulos hakutulos = null;
        try {
            hakutulos = getCached(urlConfiguration.url("organisaatio-service.hae", buildParams(searchCriteria)), OrganisaatioHakutulos.class);
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

    private Map buildParams(OrganisaatioSearchCriteria criteria) throws UnsupportedEncodingException {
        Map params = new HashMap();

        if (criteria.getOppilaitosTyyppi() != null && !criteria.getOppilaitosTyyppi().isEmpty()) {
            params.put("oppilaitosTyyppi", criteria.getOppilaitosTyyppi().toArray()[0]);
        }
        if (isNotBlank(criteria.getOrganisaatioTyyppi())) {
            params.put("organisaatioTyyppi", criteria.getOrganisaatioTyyppi());
        }
        for (String orgOid : criteria.getOidRestrictionList()) {
            params.put("oidRestrictionList", orgOid);
        }
        if (isNotBlank(criteria.getSearchStr())) {
            params.put("searchStr", criteria.getSearchStr());
        }
        params.put("skipParents",criteria.getSkipParents());
        params.put("suunnitellut",criteria.getSuunnitellut());
        params.put("vainLakkautetut",criteria.getVainLakkautetut());
        params.put("vainAktiiviset",criteria.getVainAktiiviset());
        return params;
    }

    @Override
    public List<String> findParentOids(final String organizationOid) throws IOException {
        List<String> parents = Lists.newArrayList(getCached(urlConfiguration.url("organisaatio-service.parentoids", organizationOid), String.class).split("/"));
        for (String parent : parents) {
            String prefix = parent.substring(0, ORGANIZATION_PREFIX.length());
            String suffix = parent.substring(ORGANIZATION_PREFIX.length(), parent.length());
            if (!ORGANIZATION_PREFIX.equals(prefix) && !SUFFIX_PATTERN.matcher(suffix).matches()) {
                throw new ResourceNotFoundException(
                        String.format("Getting organization parentoids for %s failed. '%s' doesn't look like oid",
                                organizationOid, parent));
            }
        }
        return parents;
    }

    @Override
    public Organization findByOid(String oid) throws IOException {
        OrganizationRestDTO organisaatioRDTO = getCached(urlConfiguration.url("organisaatio-service.byOid", oid), OrganizationRestDTO.class);
        return new Organization(organisaatioRDTO);
    }

    @Override
    public List<Organization> findByOppilaitosnumero(List<String> oppilaitosnumeros) {
        List<Organization> orgs = new ArrayList<Organization>(oppilaitosnumeros.size());
        int i = 1;
        String numero = null;
        try {
            LOG.debug("Getting {} oppilaitosnumeros", oppilaitosnumeros.size());
            for (String nro : oppilaitosnumeros) {
                numero = nro;
                LOG.debug("Getting oppilaitosnumero {} ({} / {})", numero, i++, oppilaitosnumeros.size());
                orgs.add(findByOid(numero));
            }
            LOG.debug("Got numbers");
            return orgs;
        } catch (IOException e) {
            LOG.error("Couldn't find organization for oppilaitosnumero ("+ numero +")", e);
        }
        return null;
    }

    @Override
    public List<OrganizationGroupRestDTO> findGroups(String term) throws IOException {
        OrganizationGroupListRestDTO groupList = getCached(urlConfiguration.url("organisaatio-service.ryhmat"), OrganizationGroupListRestDTO.class);
        List<OrganizationGroupRestDTO> groups = groupList.getGroups();
        List<OrganizationGroupRestDTO> filtered = new ArrayList<OrganizationGroupRestDTO>();
        String termLC = term.toLowerCase();
        for (OrganizationGroupRestDTO group : groups) {
            for (String translation : group.getNimi().getAvailableTranslations()) {
                String nameLC = translation.toLowerCase();
                if (nameLC.contains(termLC)) {
                    filtered.add(group);
                    break;
                }
            }
        }
        return filtered;
    }

    private <T> T getCached(String url, Class<T> resultType) throws IOException {
        if (cache.containsKey(url)) {
            LOG.debug("Hit cache, url: {}", url);
            Object result = cache.get(url).get();

            if (null != result && resultType.isAssignableFrom(result.getClass())) {
                return resultType.cast(result);
            }
            LOG.debug("Cache reference for key {} is stale or unassignable. Result object was of type {} expected {}", url, null == result ? null : result.getClass(), resultType.getClass());
        }

        T result = get(url, resultType);
        cache.put(url, new SoftReference<Object>(result));
        return result;

    }

    private <T> T get(String url, Class<T> resultType) throws IOException {
        HttpGet get = new HttpGet(url);
        get.setHeader("clientSubSystemCode", callerId);
        get.setHeader("Caller-Id", callerId);
        try {
            HttpResponse response = this.httpClient.execute(get);
            StatusLine status = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            if (404 == status.getStatusCode() || null == entity) {
                throw new ResourceNotFoundException("fetch failed. url: " + url +
                        " statusCode: " + status.getStatusCode() +
                        " reason: " + status.getReasonPhrase());
            }
            if (status.getStatusCode() >= 300) {
                throw new HttpResponseException(status.getStatusCode(), status.getReasonPhrase());
            }
            if (resultType.isAssignableFrom(String.class)) {
                return resultType.cast(EntityUtils.toString(entity, "UTF-8"));
            }
            InputStream in = entity.getContent();
            try {
                T t = gson.fromJson(new InputStreamReader(in), resultType);
                EntityUtils.consume(entity);
                return t;
            } catch (JsonSyntaxException jse) {
                LOG.error("Deserializing organisation failed. url: " + url);
                throw jse;
            } finally {
                in.close();
            }
        } finally {
            get.releaseConnection();
        }
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
