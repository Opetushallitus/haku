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

import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jsoup.Jsoup;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.google.api.client.repackaged.com.google.common.base.Strings.*;
import static com.google.api.client.repackaged.com.google.common.base.Strings.isNullOrEmpty;

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
        String[] langPreferences = (String[]) ArrayUtils.addAll(new String[]{language}, LANGS);
        for (String lang : langPreferences) {
            String translation = StringUtils.trimToEmpty(translations.get(lang));
            if (isEmpty(translation))
                return translation;
        }
        if(!translations.isEmpty())
            return translations.values().iterator().next();
        return "";
    }

    private boolean isEmpty(String translation) {
        boolean isEmpty;
        boolean isXmlOrHtml = translation.startsWith("<");
        if(isXmlOrHtml) {
            isEmpty = isNullOrEmpty(StringUtils.trimToEmpty(Jsoup.parse(translation).text()));
        } else {
            isEmpty = isNullOrEmpty(translation);
        }
        return isEmpty;
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
