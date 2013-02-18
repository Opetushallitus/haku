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

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.oppija.lomake.domain.I18nText;
import fi.vm.sade.oppija.lomake.domain.elements.Element;
import fi.vm.sade.oppija.lomake.domain.elements.custom.PreferenceRow;

import java.util.HashMap;
import java.util.Map;

public final class ElementUtil {
    private ElementUtil() {
    }

    public static I18nText createI18NText(final String text) {
        return new I18nText("text_" + Long.toString(System.currentTimeMillis()),
                ImmutableMap.of("fi", text, "sv", text + "_sv", "en", text + "_en"));
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
