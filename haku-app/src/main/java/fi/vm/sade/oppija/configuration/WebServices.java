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
package fi.vm.sade.oppija.configuration;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;

@Configuration
@Profile("default")
public class WebServices {

    @Bean(name="organisaatioService")
    public OrganisaatioService getOrganisaatioService(@Value("${organisaatio.webservice.url.backend}") String url) {
        return getProxy(OrganisaatioService.class, url);
    }

    @Bean(name="koodiPublicService")
    public KoodiService getKoodiService(@Value("${koodi.public.webservice.url.backend}") String url) {
        return getProxy(KoodiService.class, url);
    }

    @SuppressWarnings("unchecked")
    private <T> T getProxy(final Class<T> type, final String url) {
        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(type);
        factory.setAddress(url);
        return (T) factory.create();
    }
}
