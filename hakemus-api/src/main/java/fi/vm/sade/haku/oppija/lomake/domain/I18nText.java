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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jsoup.Jsoup;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.api.client.repackaged.com.google.common.base.Strings.*;
import static com.google.api.client.repackaged.com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.*;
import static java.util.stream.Collectors.*;

public class I18nText implements Serializable {

    private static final long serialVersionUID = 3485756393751579235L;

    public static final ArrayList<String> LANGS = newArrayList("fi", "sv", "en");

    private final Map<String, String> translations;


    public I18nText(@JsonProperty(value = "translations") final Map<String, String> translations) {
        this.translations = Collections.unmodifiableMap(translations);
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public String getText(String language) {
        Stream<Map.Entry<String, String>> entries = translations.entrySet().stream();
        ToIntFunction<String> isTargetLanguage = l -> language.equals(l) ? 0 : 1;
        ToIntFunction<String> indexOfLanguage = l -> {
            int indexOfLang = LANGS.indexOf(l);
            if(indexOfLang == -1) {
                return LANGS.size();
            } else {
                return indexOfLang;
            }
        };

        List<Map.Entry<String, String>>
                nonNullEntries =
                entries.filter(e -> e.getKey() != null)
                        .filter(nonNullValue()).sorted(
                        (o1,o2) -> new CompareToBuilder()
                                // is target language
                                .append(isTargetLanguage.applyAsInt(o1.getKey()),
                                        isTargetLanguage.applyAsInt(o2.getKey()))
                                // or use language index to sorting
                                .append(indexOfLanguage.applyAsInt(o1.getKey()),
                                        indexOfLanguage.applyAsInt(o2.getKey())).build()
                ).collect(toList());

        if(!nonNullEntries.isEmpty()) {
            return nonNullEntries.iterator().next().getValue();
        } else {
            return "";
        }
    }

    private Predicate<Map.Entry<String, String>> nonNullValue() {
        return e -> !isEmpty(e.getValue());
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
