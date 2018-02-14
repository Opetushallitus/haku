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

package fi.vm.sade.haku.oppija.ui;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.haku.virkailija.authentication.AuthenticationService;
import fi.vm.sade.haku.virkailija.authentication.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.ws.rs.core.Context;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class LocaleFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LocaleFilter.class);

    private static final Locale DEFAULT_LOCALE = new Locale("fi");
    public static final String LANGUAGE_QUERY_PARAMETER_KEY = "lang";

    final HttpServletRequest httpServletRequest;

    @InjectParam
    AuthenticationService authenticationService;

    @Autowired
    public LocaleFilter(@Context final HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }


    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {
        HttpSession session = httpServletRequest.getSession();
        String lang = getLanguage(containerRequest);

        Locale currentLocale = (Locale) Config.get(session, Config.FMT_LOCALE);
        Locale newLocale = getNewLocale(lang, currentLocale);
        Config.set(session, Config.FMT_LOCALE, newLocale);
        Config.set(session, Config.FMT_FALLBACK_LOCALE, DEFAULT_LOCALE);
        Config.set(httpServletRequest, Config.FMT_LOCALE, newLocale);
        Config.set(httpServletRequest, Config.FMT_FALLBACK_LOCALE, DEFAULT_LOCALE);
        httpServletRequest.setAttribute("fi_vm_sade_oppija_language", newLocale.getLanguage());
        return containerRequest;
    }

    private String getLanguage(ContainerRequest containerRequest) {
        String lang = containerRequest.getQueryParameters().getFirst(LANGUAGE_QUERY_PARAMETER_KEY);

        if (!isBlank(lang)) {
            return lang;
        }

        String host = containerRequest.getHeaderValue("Host");
        log.info("HOST: " + host);
        if (host != null && host.endsWith("studieinfo.fi")) {
            lang = "sv";
        } else if (host != null && host.endsWith("studyinfo.fi")) {
            lang = "en";
        } else {
            lang = "fi";
        }

        Person person = authenticationService.getCurrentHenkilo();
        String personOid = person != null ? person.getPersonOid() : "null";
        log.debug("Got person: " + personOid);
        if (person != null) {
            String contactLang = person.getContactLanguage();
            log.debug("Person contactLang: " + contactLang);
            if ("fi".equals(contactLang) || "sv".equals(contactLang) || "en".equals(contactLang)) {
                lang = contactLang;
            }
        }

        return lang;
    }

    private Locale getNewLocale(final String lang, final Locale currentLocale) {
        Locale locale;
        if (lang != null) {
            locale = new Locale(lang);
        } else if (currentLocale != null) {
            locale = currentLocale;
        } else {
            locale = DEFAULT_LOCALE;
        }
        return locale;
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
}
