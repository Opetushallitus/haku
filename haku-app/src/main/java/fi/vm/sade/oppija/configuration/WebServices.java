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

import fi.vm.sade.authentication.service.AuthenticationService;
import fi.vm.sade.authentication.service.PersonalInformationService;
import fi.vm.sade.koodisto.util.CachingKoodistoClient;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
public class WebServices {

    @Bean(name="organisaatioService")
    public OrganisaatioService getOrganisaatioService(@Value("${organisaatio.webservice.url.backend}") String url) {
        return getProxy(OrganisaatioService.class, url);
    }

    @Bean(name="personalInformationService")
    public PersonalInformationService getPersonalInformationService(@Value("${personalInformation.webservice.url.backend}") String url) {
        return getProxy(PersonalInformationService.class, url);
    }

    @Bean(name="authenticationService")
    public AuthenticationService getAuthenticationService(@Value("${authentication.webservice.url.backend}") String url) {
        return getProxy(AuthenticationService.class, url);
    }

    @Bean(name="cachingKoodistoClient")
    public CachingKoodistoClient getCachingKoodistoClient() {
        return new CachingKoodistoClient();
    }

    @SuppressWarnings("unchecked")
    private <T> T getProxy(final Class<T> type, final String url) {
        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(type);
        factory.setAddress(url);
        return (T) factory.create();
    }
}
