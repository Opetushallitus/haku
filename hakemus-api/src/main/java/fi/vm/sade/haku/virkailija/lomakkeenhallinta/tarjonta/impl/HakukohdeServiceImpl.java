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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.tarjonta.HakukohdeService;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeHakutulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakutuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.TarjoajaHakutulosV1RDTO;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
//@Profile(value = {"default", "devluokka"})
public class HakukohdeServiceImpl implements HakukohdeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HakukohdeServiceImpl.class);
    public static final String MEDIA_TYPE = MediaType.APPLICATION_JSON + ";charset=UTF-8";
    private final WebResource hakukohdeResource;
    private final WebResource hakukohdeV1Resource;
    private final String SEARCH_PATH = "search";
    private final String PARAM_GROUP_OID = "organisaatioRyhmaOid";
    private final String PARAM_APPLICATION_SYSTEM_OID= "hakuOid";

    private static final Map<String, SoftReference<HakukohdeDTO>> cache = new HashMap<String, SoftReference<HakukohdeDTO>>();

    @Autowired
    public HakukohdeServiceImpl(@Value("${tarjonta.hakukohde.resource.url}") final String tarjontaHakukohdeResourceUrl, @Value("${tarjonta.v1.hakukohde.resource.url}") final String tarjontaV1HakukohdeResourceUrl) {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client clientWithJacksonSerializer = Client.create(cc);
        hakukohdeResource = clientWithJacksonSerializer.resource(tarjontaHakukohdeResourceUrl);
        hakukohdeV1Resource = clientWithJacksonSerializer.resource(tarjontaV1HakukohdeResourceUrl);
        LOGGER.debug("Tarjonnan hakukohde uri: {}. Tarjonnan v1 hakukohde uri: {} ",hakukohdeResource.getURI().toString(), hakukohdeV1Resource.getURI().toString());
    }

    @Override
    public HakukohdeDTO findByOid(String oid){
        SoftReference<HakukohdeDTO> cacheReference = cache.get(oid);
        HakukohdeDTO hakukohde = null == cacheReference ? null : cacheReference.get();
        if (null != hakukohde){
            return hakukohde;
        }
        hakukohde = fetchByOid(oid);
        if (null != hakukohde){
            cache.put(oid, new SoftReference<HakukohdeDTO>(hakukohde));
        }
        return hakukohde;
    }

    @Override
    public List<String> findByGroupAndApplicationSystem(String applicationOptionGroupId, String applicationSystemId) {
        //TODO: cache
        ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> searchResults = fetchByGroupAndApplicationSystem(applicationOptionGroupId, applicationSystemId);
        LOGGER.debug("With option group {} and applicationSystem {}: Got {} results", applicationOptionGroupId, applicationSystemId, searchResults.getResult().getTuloksia());
        HashSet<String> applicationOptions = new HashSet<String>();
        for (TarjoajaHakutulosV1RDTO<HakukohdeHakutulosV1RDTO> tarjoajaHakutulos : searchResults.getResult().getTulokset()) {
            LOGGER.debug("With option group {} and applicationSystem {}: Got Provider Id {}, name {} with {} results", applicationOptionGroupId, applicationSystemId, tarjoajaHakutulos.getOid(), tarjoajaHakutulos.getNimi().toString(), tarjoajaHakutulos.getTulokset().size());
            for (HakukohdeHakutulosV1RDTO hakukohdeHakutulosV1RDTO : tarjoajaHakutulos.getTulokset()) {
                if (!applicationSystemId.equals(hakukohdeHakutulosV1RDTO.getHakuOid()))
                    LOGGER.error("With  option group {} and applicationSystem {}: Data error. Got {} while expecting {}", hakukohdeHakutulosV1RDTO.getHakuOid(), applicationSystemId,hakukohdeHakutulosV1RDTO.getHakuOid(), applicationSystemId);
                else {
                    applicationOptions.add(hakukohdeHakutulosV1RDTO.getOid());
                }
            }
        }
        return new ArrayList(applicationOptions);
    }

    private HakukohdeDTO fetchByOid(String oid){
        WebResource asWebResource = hakukohdeResource.path(oid);
        return asWebResource.accept(MEDIA_TYPE).get(new GenericType<HakukohdeDTO>() {
        });
    }

    private ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> fetchByGroupAndApplicationSystem(String groupOid, String applicationSystemId){
        WebResource asWebResource = hakukohdeV1Resource.path(SEARCH_PATH).queryParam(PARAM_GROUP_OID, groupOid).queryParam(PARAM_APPLICATION_SYSTEM_OID, applicationSystemId);
        LOGGER.debug("With option group {} and applicationSystem {}: Using resource {}", groupOid, applicationSystemId, asWebResource);
        return asWebResource.accept(MEDIA_TYPE).get(new GenericType<ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>>>() {
        });
    }
}
