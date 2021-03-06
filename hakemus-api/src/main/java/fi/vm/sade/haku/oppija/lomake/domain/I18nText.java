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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jsoup.Jsoup;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.api.client.repackaged.com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

public class I18nText implements Serializable {

    private static final long serialVersionUID = 3485756393751579235L;
    @JsonIgnore
    public static final ArrayList<String> LANGS = newArrayList("fi", "sv", "en");

    @JsonIgnore
    private static final ToIntFunction<String> orderByLanguages = l -> {
        int indexOfLang = LANGS.indexOf(l);
        if(indexOfLang == -1) {
            return LANGS.size();
        } else {
            return indexOfLang;
        }
    };

    private final Map<String, String> translations;

    public Map<String, String> getTranslations() {
        return I18nText.copyWithDefaultTranslations(this).translations;
    }

    public I18nText(@JsonProperty(value = "translations") final Map<String, String> translations) {
        if(translations == null) {
            this.translations = emptyMap();
        } else {
            Stream<Map.Entry<String, String>> entryStream = translations.entrySet().stream()
                    .filter(e -> e.getKey() != null)
                    .filter(nonNullValue());
            Map<String, String> filteredCollection = entryStream.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
            if(filteredCollection.isEmpty()) {
                this.translations = emptyMap();
            } else {
                this.translations = unmodifiableMap(filteredCollection);
            }
        }
    }
    @JsonIgnore
    public boolean isEmpty() {
        return this.translations.isEmpty();
    }
    @JsonIgnore
    public Collection<String> getAvailableLanguages() {
        return this.translations.keySet();
    }
    @JsonIgnore
    public Collection<String> getAvailableTranslations() {
        return this.translations.values();
    }
    @JsonIgnore
    public String getTextOrNull(String language) {
        return translations.get(language);
    }
    @JsonIgnore
    public String getText(String language) {
        ToIntFunction<String> isTargetLanguage = orderBySpecificLanguage(language);
        return translations.entrySet().stream().sorted(
                (o1,o2) -> new CompareToBuilder()
                        // is target language
                        .append(isTargetLanguage.applyAsInt(o1.getKey()),
                                isTargetLanguage.applyAsInt(o2.getKey()))
                        // or use language index to sorting
                        .append(orderByLanguages.applyAsInt(o1.getKey()),
                                orderByLanguages.applyAsInt(o2.getKey())).build()
        ).map(e -> e.getValue()).findFirst().orElse("");
    }

    private ToIntFunction<String> orderBySpecificLanguage(String language) {
        return l -> language.equals(l) ? 0 : 1;
    }
    private Predicate<Map.Entry<String, String>> nonNullValue() {
        return e -> !isEmpty(e.getValue());
    }
    public boolean containsAllDefaultLanguages() {
        return this.translations.keySet().containsAll(LANGS);
    }
    private boolean isEmpty(String translation) {
        if(translation == null) {
            return true;
        }
        boolean isEmpty;
        boolean isXmlOrHtml = translation.startsWith("<");
        if(isXmlOrHtml) {
            isEmpty = isNullOrEmpty(StringUtils.trimToEmpty(Jsoup.parse(translation).text()));
        } else {
            isEmpty = isNullOrEmpty(StringUtils.trimToEmpty(translation));
        }
        return isEmpty;
    }

    @Override
    public String toString() {
        if(translations == null) return null;
        return translations.toString();
    }

    public static I18nText copyWithDefaultTranslations(I18nText obj) {
        if(obj == null || obj.isEmpty()) {
            return new I18nText(emptyMap());
        } else {
            if(obj.containsAllDefaultLanguages()) {
                return obj;
            } else {
                return new I18nText(LANGS.stream().collect(Collectors.toMap(l -> l, obj::getText)));
            }
        }
    }

    public static I18nText copy(I18nText obj) {
        return obj;
    }
    public I18nText prepend(String prefix) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        this.translations.forEach((key,val) -> builder.put(key, prefix + val));
        return new I18nText(builder.build());
    }
    public static boolean compare(I18nText obj1, I18nText obj2) {
        if(obj1 == null && obj2 != null) {
            return false;
        } else if(obj1 != null && obj2 == null) {
            return false;
        } else {
            if(obj1 == obj2 || obj1.translations == obj2.translations) {
                return true;
            }
            boolean equalSizes = obj1.translations.size() == obj2.translations.size();
            if(!equalSizes) {
                return false;
            }
            boolean equalObjects = obj1.translations.entrySet().containsAll(obj2.translations.entrySet());
            return equalObjects;
        }
    }
    public int size() {
        return translations.size();
    }

}
