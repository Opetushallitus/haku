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

import com.google.common.collect.ImmutableMap;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Map;

public class I18nText implements Serializable {

    private static final long serialVersionUID = 3485756393751579235L;
    public static final String[] LANGS = {"fi", "sv", "en"};
    private final Map<String, String> translations;


    public I18nText(@JsonProperty(value = "translations") final Map<String, String> translations) {
        this.translations = translations;
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
}
