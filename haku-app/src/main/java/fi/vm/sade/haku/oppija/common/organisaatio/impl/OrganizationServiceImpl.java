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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import fi.vm.sade.haku.oppija.common.HttpClientHelper;
import fi.vm.sade.haku.oppija.common.organisaatio.Organization;
import fi.vm.sade.haku.oppija.common.organisaatio.OrganizationService;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.api.search.OrganisaatioSearchCriteria;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private HttpClientHelper clientHelper;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static final int MAX_RESULTS = 10000;
    private final OrganisaatioSearchService service;

    @Autowired
    public OrganizationServiceImpl(final OrganisaatioSearchService service) {
        this.service = service;
    }

    @Override
    public List<Organization> search(final OrganisaatioSearchCriteria searchCriteria) {

        LOG.debug("search organization kunta: {}, oidRestrictions: {}, loiType: {}, orgType: {}, q: {}, skipParents: {}",
                searchCriteria.getKunta(),
                searchCriteria.getOidRestrictionList().size(),
                searchCriteria.getOppilaitosTyyppi(),
                searchCriteria.getOrganisaatioTyyppi(),
                searchCriteria.getSearchStr(),
                searchCriteria.getSkipParents());

        final List<OrganisaatioPerustieto> result = service.searchBasicOrganisaatios(searchCriteria);

        LOG.debug("Criteria: {}, found {} organizations", searchCriteria, result.size());
        return Lists.newArrayList(Lists.transform(result, new OrganisaatioPerustietoToOrganizationFunction()));
    }

    @Override
    public List<String> findParentOids(final String organizationOid) {
        return service.findParentOids(organizationOid);
    }

    @Override
    public Organization findByOppilaitosnumero(String oppilaitosnumero) {
        String url = getClientHelper().getRealUrl(oppilaitosnumero);

        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(url);

        try {
            client.executeMethod(get);
        } catch (IOException e) {
            return null;
        }
        int status = get.getStatusCode();
        if (status == 200) {
            String responseString = null;
            try {
                responseString = get.getResponseBodyAsString();
                JsonElement orgJson = new JsonParser().parse(responseString);
                return deserialize(orgJson);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return null;
    }

    public Organization deserialize(JsonElement json) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String oid = obj.get("oid").getAsString();
        String parentOid = obj.get("parentOid").getAsString();
        Iterator<JsonElement> tyypitIter = obj.get("tyypit").getAsJsonArray().iterator();
        List<String> types = new ArrayList<String>();

        while (tyypitIter.hasNext()) {
            types.add(tyypitIter.next().getAsString());
        }

        JsonObject nimiObj = obj.get("nimi").getAsJsonObject();
        HashMap<String, String> translations = new HashMap<String, String>();
        for (Map.Entry<String, JsonElement> entry : nimiObj.entrySet()) {
            translations.put(entry.getKey(), entry.getValue().getAsString());
        }
        I18nText name = new I18nText(translations);

        Date startDate = null;
        try {
            startDate = dateFormat.parse(obj.get("alkuPvm").getAsString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = null;
        try {
            JsonElement loppuPvmObj = obj.get("loppuPvm");
            if (loppuPvmObj != null) {
                endDate = dateFormat.parse(loppuPvmObj.getAsString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Organization(name, oid, parentOid, types, startDate, endDate);
    }

    private HttpClientHelper getClientHelper() {
        if (clientHelper == null) {
            this.clientHelper = new HttpClientHelper(casUrl, targetService, "/rest/organisaatio/", clientAppUser, clientAppPass);
        }
        return clientHelper;
    }
}
