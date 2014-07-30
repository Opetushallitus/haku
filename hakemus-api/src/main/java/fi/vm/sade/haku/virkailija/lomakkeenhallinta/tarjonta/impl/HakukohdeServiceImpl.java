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
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

@Service
//@Profile(value = {"default", "devluokka"})
public class HakukohdeServiceImpl implements HakukohdeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HakukohdeServiceImpl.class);
    public static final String MEDIA_TYPE = MediaType.APPLICATION_JSON + ";charset=UTF-8";
    private final WebResource webResource;

    private static final Map<String, SoftReference<HakukohdeDTO>> cache = new HashMap<String, SoftReference<HakukohdeDTO>>();

    @Autowired
    public HakukohdeServiceImpl(@Value("${tarjonta.hakukohde.resource.url}") final String tarjontaHakukohdeResourceUr) {
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client clientWithJacksonSerializer = Client.create(cc);
        webResource = clientWithJacksonSerializer.resource(tarjontaHakukohdeResourceUr);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Tarjonnan hakukohde uri: " + webResource.getURI().toString());
        }
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

    private HakukohdeDTO fetchByOid(String oid){
        WebResource asWebResource = webResource.path(oid);
        return asWebResource.accept(MEDIA_TYPE).get(new GenericType<HakukohdeDTO>() {
        });
    }
}
