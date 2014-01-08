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
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TranslationsUtil {

    final static List<String> langs = new ArrayList<String>(3);

    static {
        langs.add(KieliType.FI.value().toLowerCase());
        langs.add(KieliType.SV.value().toLowerCase());
        langs.add(KieliType.EN.value().toLowerCase());
    }

    private TranslationsUtil() { // NOSONAR
    }

    public static Map<String, String> createTranslationsMap(final KoodiType koodiType) {
        List<KoodiMetadataType> metadata = koodiType.getMetadata();
        Map<String, String> translations = new HashMap<String, String>();
        for (KoodiMetadataType koodiMetadataType : metadata) {
            translations.put(koodiMetadataType.getKieli().value().toLowerCase(), koodiMetadataType.getNimi());
        }
        return createTranslationsMap(translations);
    }

    public static Map<String, String> createTranslationsMap(final Map<String, String> partialTranslations) {
        for (String lang : langs) {
            if (partialTranslations.get(lang) == null) {
                for (String tryLang : langs) {
                    if (partialTranslations.get(tryLang) != null) {
                        partialTranslations.put(lang, partialTranslations.get(tryLang) + " (" + tryLang + ")");
                        break;
                    }
                }
            }
        }

        return ImmutableMap.copyOf(partialTranslations);
    }
}
