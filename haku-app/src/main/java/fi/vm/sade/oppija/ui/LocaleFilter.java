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

package fi.vm.sade.oppija.ui;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.ws.rs.core.Context;
import java.util.Locale;

public class LocaleFilter implements ContainerRequestFilter {

    private static final Locale DEFAULT_LOCALE = new Locale("fi");
    public static final String LANGUAGE_COOKIE_KEY = "i18next";
    public static final String LANGUAGE_QUERY_PARAMETER_KEY = "lang";

    final HttpServletRequest httpServletRequest;

    public LocaleFilter(@Context final HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {
        HttpSession session = httpServletRequest.getSession();
        //String lang = getLangQueryParameter(containerRequest);
        //Locale currentLocale = (Locale) Config.get(session, Config.FMT_LOCALE);
        Locale newLocale = DEFAULT_LOCALE;//Locale newLocale = getNewLocale(lang, currentLocale);
        Config.set(session, Config.FMT_LOCALE, newLocale);
        httpServletRequest.setAttribute("fi_vm_sade_oppija_language", newLocale.getLanguage());
        return containerRequest;
    }

    /*private Locale getNewLocale(final String lang, final Locale currentLocale) {
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

    private String getLangQueryParameter(final ContainerRequest containerRequest) {
        String lang = containerRequest.getQueryParameters().getFirst(LANGUAGE_QUERY_PARAMETER_KEY);
        if (lang != null) {
            return lang;
        }
        return containerRequest.getCookieNameValueMap().getFirst(LANGUAGE_COOKIE_KEY);
    } */
}
