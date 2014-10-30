/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.impl;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakuService;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OidV1RDTO;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Mikko Majapuro
 */
@Service
@Profile(value = {"default", "devluokka"})
public class HakuServiceImpl implements HakuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HakuServiceImpl.class);
    public static final String MAX_COUNT = "10000"; // Tarjonta ei hyväksi -1:stä ja hajoaa Integer.MAX_VALUE:een.
    public static final String COUNT_PARAMETER = "count";
    public static final String MEDIA_TYPE = MediaType.APPLICATION_JSON + ";charset=UTF-8";
    public static final String HAKUKOHDE = "hakukohde";
    private final WebResource webResource;

    @Autowired
    public HakuServiceImpl(@Value("${tarjonta.v1.hakukohde.resource.urll}") final String tarjontaHakuResourceUrl) {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client clientWithJacksonSerializer = Client.create(cc);
        webResource = clientWithJacksonSerializer.resource(tarjontaHakuResourceUrl).queryParam(COUNT_PARAMETER, MAX_COUNT); // todo pagination
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Tarjonnan uri: " + webResource.getURI().toString());
        }
    }

    @Override
    public List<ApplicationSystem> getApplicationSystems() {
        List<OidRDTO> hakuOids = webResource.accept(MEDIA_TYPE).get(new GenericType<List<OidRDTO>>() {
        });
        List<HakuV1RDTO> hakuDTOs = Lists.newArrayList();
        if (hakuOids != null) {
            for (OidRDTO oid : hakuOids) {
                HakuV1RDTO haku = fetchApplicationSystem(oid.getOid());
                if (haku.isJarjestelmanHakulomake()) {
                    hakuDTOs.add(haku);
                }
            }
        }
        return Lists.transform(hakuDTOs, new HakuV1RDTOToApplicationSystemFunction());
    }

    @Override
    public ApplicationSystem getApplicationSystem(String oid) {
        HakuV1RDTO hakuDTO = fetchApplicationSystem(oid);
        return new HakuV1RDTOToApplicationSystemFunction().apply(hakuDTO);
    }

    @Override
    public List<String> getRelatedApplicationOptionIds(String oid){
        List<OidV1RDTO> applicationOptionOidRDTOs = fetchApplicationOptions(oid);
        LOGGER.debug("Got " + (null == applicationOptionOidRDTOs ? null: applicationOptionOidRDTOs.size()) + " with application system id "+ oid);
        List<String> applicationOptionOids = Lists.newArrayList();
        if (applicationOptionOidRDTOs != null) {
            for (OidV1RDTO optionOid : applicationOptionOidRDTOs) {
                applicationOptionOids.add(optionOid.getOid());
            }
        }
        return applicationOptionOids;
    }

    private List<OidV1RDTO> fetchApplicationOptions(String oid){
        WebResource asWebResource = webResource.path(oid).path(HAKUKOHDE);
        LOGGER.debug("Requesting " + asWebResource.getURI());
        return asWebResource.accept(MEDIA_TYPE).get(new GenericType<List<OidV1RDTO>>() {
        });
    }

    private HakuV1RDTO fetchApplicationSystem(String oid) {
        WebResource asWebResource = webResource.path(oid);
        return asWebResource.accept(MEDIA_TYPE).get(new GenericType<HakuV1RDTO>() {
        });
    }
}
