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

package fi.vm.sade.haku.oppija.lomake.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class I18nText implements Serializable {

    private static final long serialVersionUID = 3485756393751579235L;
    public static final String[] LANGS = {"fi", "sv", "en"};

    private final Map<String, String> translations;


    public I18nText(@JsonProperty(value = "translations") final Map<String, String> translations) {
        this.translations = Collections.unmodifiableMap(translations);
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public String getText(String language) {
        String text = translations.get(language);
        if (text != null) return text;
        text = translations.get("fi");
        if (text != null) return text;
        return "";
    }

    @Override
    public String toString() {
        return translations.toString();
    }

    public static I18nText copy(I18nText obj) {
        if(obj != null) {
            Map copy = new HashMap();
            copy.putAll(obj.getTranslations());
            return new I18nText(copy);
        }
        return null;
    }

    public static boolean compare(I18nText obj1, I18nText obj2) {
        if(obj1 == null && obj2 != null) {
            return false;
        } else if(obj1 != null && obj2 == null) {
            return false;
        } else if(obj1 != null && obj2 != null && !obj1.getTranslations().equals(obj2.getTranslations())) {
            return false;
        }
        return true;
    }

}
