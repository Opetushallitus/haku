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
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Profile(value = {"default", "devluokka"})
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private static Map<String, SoftReference<Object>> cache;
    private static final String ROOT_ORGANIZATION_OPH = "1.2.246.562.10.00000000001";
    private static final String ORGANIZATION_PREFIX = "1.2.246.562.10";
    private static final Pattern SUFFIX_PATTERN = Pattern.compile("^[0-9]{11}$");

    private HttpClient httpClient;

    private Gson gson;

    @Value("${cas.service.organisaatio-service}")
    private String targetService;

    public OrganizationServiceImpl() {
        this.cache = new HashMap<String, SoftReference<Object>>();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new TimestampDateAdapter());
        builder.registerTypeAdapter(OrganizationGroupListRestDTO.class, new OrganizationGroupListRestDTOAdapter());
        this.gson = builder.create();
    }

    @Override
    public List<Organization> search(final OrganisaatioSearchCriteria searchCriteria) throws UnsupportedEncodingException {

        String baseUrl = "/rest/organisaatio/hae";
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
        String url = "/rest/organisaatio/" + organizationOid + "/parentoids";
        List<String> parents = Lists.newArrayList(getCached(url, String.class).split("/"));
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
        String url = "/rest/organisaatio/" + oid;
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

    @Override
    public List<OrganizationGroupRestDTO> findGroups(String term) throws IOException {
        String url = "/rest/organisaatio/1.2.246.562.10.00000000001/ryhmat";
        OrganizationGroupListRestDTO groupList = getCached(url, OrganizationGroupListRestDTO.class);
        List<OrganizationGroupRestDTO> groups = groupList.getGroups();
        List<OrganizationGroupRestDTO> filtered = new ArrayList<OrganizationGroupRestDTO>();
        String termLC = term.toLowerCase();
        for (OrganizationGroupRestDTO group : groups) {
            for (Map.Entry<String, String> entry : group.getNimi().getTranslations().entrySet()) {
                String nameLC = entry.getValue().toLowerCase();
                if (nameLC.contains(termLC)) {
                    filtered.add(group);
                    break;
                }
            }
        }
        return filtered;
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

        T result = get(url, resultType);
        cache.put(url, new SoftReference<Object>(result));
        return result;

    }

    private <T> T get(String url, Class<? extends T> resultType) throws IOException {
        HttpClient client = getHttpClient();
        HttpGet get = new HttpGet(targetService + url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            if (resultType.isAssignableFrom(String.class)) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(entity.getContent(), writer, "UTF-8");
                T ret = (T) writer.toString();
                entity.consumeContent();
                get.releaseConnection();
                return ret;
            } else {
                try {
                    T ret = gson.fromJson(new InputStreamReader(entity.getContent()), resultType);
                    entity.consumeContent();
                    get.releaseConnection();
                    return ret;
                } catch (JsonSyntaxException jse) {
                    LOG.error("Deserializing organisation failed. url: "+url);
                    throw jse;
                }
            }
        }

        if (get != null) {
            get.releaseConnection();
        }
        StatusLine statusLine = response.getStatusLine();
        throw new ResourceNotFoundException("fetch failed. url: "+url+" statusCode: "+statusLine.getStatusCode()
                +" reason: "+statusLine.getReasonPhrase());
    }

    private HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
        }
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
