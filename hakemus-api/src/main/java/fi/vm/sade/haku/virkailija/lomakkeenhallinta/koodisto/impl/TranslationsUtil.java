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

package fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.util.StringUtil;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class TranslationsUtil {

    final static String LANG_CODE_PREFIX = "kieli_";

    private TranslationsUtil() { // NOSONAR
    }

    public static I18nText createTranslationsMap(final KoodiType koodiType) {
        final List<KoodiMetadataType> metadata = koodiType.getMetadata();
        final Map<String, String> translations = Maps.newHashMapWithExpectedSize(metadata.size());
        for (KoodiMetadataType koodiMetadataType : metadata) {
            translations.put(koodiMetadataType.getKieli().value().toLowerCase(), koodiMetadataType.getNimi());
        }
        return ensureDefaultLanguageTranslations(translations);
    }
    public static I18nText createTranslationsMap(final Map<String, String> translations) {
        return ensureDefaultLanguageTranslations(translations);
    }
    /*
    public static Map<String, String> ensureDefaultLanguageTranslations(final Map<String, String> translations) {
        final HashMap<String, String> ensuredTranslations = new HashMap<String, String>(translations);
        for (String langKey : langs) {
            String translation = ensuredTranslations.get(langKey);
            if (null != translation && !"".equals(translation)) {
                continue;
            }
            for (String backupLang : langs) {
                translation = ensuredTranslations.get(backupLang);
                if (null != translation && !"".equals(translation)) {
                    ensuredTranslations.put(langKey, translation);
                    break;
                }
            }
        }
        return ensuredTranslations;
    }
    */
    public static Map<String, String> filterCodePrefix(final Map<String, String> translations) {
        final HashMap<String, String> filteredTranslations = new HashMap<String, String>(translations.size());
        for (String key: translations.keySet()){
            String newKey = key.replace(LANG_CODE_PREFIX, "");
            filteredTranslations.put(newKey, translations.get(key));

        }
        return filteredTranslations;
    }
    public static I18nText ensureDefaultLanguageTranslations(final Map<String, String> translations) {
        return I18nText.copyWithDefaultTranslations(new I18nText(translations));
    }
    public static I18nText ensureDefaultLanguageTranslations(final I18nText translations) {
        return I18nText.copyWithDefaultTranslations(translations);
    }
}
