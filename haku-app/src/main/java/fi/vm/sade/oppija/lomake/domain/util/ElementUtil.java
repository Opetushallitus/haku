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

package fi.vm.sade.oppija.lomake.domain.util;

import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceRow;
import org.apache.log4j.Logger;

import java.util.*;

public final class ElementUtil {

    private static Logger log = Logger.getLogger(ElementUtil.class);

    private ElementUtil() {
    }

    public static I18nText createI18NText(final String text) {
        return createI18NText(text, "form_messages", "fi", "en", "sv");
    }

    public static I18nText createI18NTextError(final String text) {
        return createI18NText(text, "form_errors", "fi", "en", "sv");
    }

    public static I18nText createI18NText(final String text, final String bundleName) {
        return createI18NText(text, bundleName, "fi", "en", "sv");
    }

    public static I18nText createI18NText(final String key, final String bundleName, final String... langs) {
        Map<String, String> translations = new HashMap<String, String>();
        for (String lang : langs) {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, new Locale(lang));

            String text = null;
            try {
                text = bundle.getString(key);
            } catch (MissingResourceException mre) {
                text = key + "[" + lang + "]";
                log.warn("No translation found for key '" + key + "' in " + lang);
            }
            translations.put(lang, text);
        }
        return new I18nText(key + Long.toString(System.currentTimeMillis()), translations);
    }

    public static PreferenceRow createI18NPreferenceRow(final String id, final String title) {
        return new PreferenceRow(id,
                createI18NText(title),
                createI18NText("Tyhjennä"),
                createI18NText("Koulutus"),
                createI18NText("Opetuspiste"), "Valitse koulutus");
    }

    public static <E extends Element> Map<String, E> findElementsByType(Element element, Class<E> eClass) {
        Map<String, E> elements = new HashMap<String, E>();
        findElementByType(element, elements, eClass);
        return elements;
    }

    private static <E extends Element> void findElementByType(final Element element, final Map<String, E> elements, Class<E> eClass) {
        if (element.getClass().isAssignableFrom(eClass)) {
            elements.put(element.getId(), (E) element);
        }
        for (Element child : element.getChildren()) {
            findElementByType(child, elements, eClass);
        }
    }

}
