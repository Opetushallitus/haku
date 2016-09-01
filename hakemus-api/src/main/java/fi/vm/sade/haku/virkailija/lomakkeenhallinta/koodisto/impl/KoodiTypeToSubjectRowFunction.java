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

import com.google.common.base.Function;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.util.KoodistoClient;

import java.util.List;
import java.util.Map;

public class KoodiTypeToSubjectRowFunction implements Function<KoodiType, SubjectRow> {

    public static final String CODE_OPPIAINEENVALINNAISUUS = "oppiaineenvalinnaisuus";
    public static final String CODE_ONLUKIONOPPIAINE = "onlukionoppiaine";
    public static final String CODE_ONPERUSASTEENOPPIAINE = "onperusasteenoppiaine";
    public static final String CODE_OPPIAINEENKIELISYYS = "oppiaineenkielisyys";
    public static final String CODE_VALUE_TRUE = "1";
    private final KoodistoClient koodiService;

    public KoodiTypeToSubjectRowFunction(final KoodistoClient koodiService) {
        this.koodiService = koodiService;
    }

    @Override
    public SubjectRow apply(final KoodiType koodiType) {
        I18nText translationsMap = TranslationsUtil.createTranslationsMap(koodiType);
        String koodiArvo = koodiType.getKoodiArvo();

        boolean optional = false;
        boolean highSchool = false;
        boolean comprehensiveSchool = false;
        boolean language = false;

        KoodiUriAndVersioType koodi = new KoodiUriAndVersioType();
        koodi.setKoodiUri(koodiType.getKoodiUri());
        koodi.setVersio(koodiType.getVersio());
        if (koodiService != null) {
            List<KoodiType> koodiTypes = koodiService.getAlakoodis(koodi.getKoodiUri());
            for (KoodiType type : koodiTypes) {
                String koodistoUri = type.getKoodisto().getKoodistoUri();
                if (CODE_OPPIAINEENVALINNAISUUS.equals(koodistoUri)) {
                    optional = isTrue(type);
                } else if (CODE_ONLUKIONOPPIAINE.equals(koodistoUri)) {
                    highSchool = isTrue(type);
                } else if (CODE_ONPERUSASTEENOPPIAINE.equals(koodistoUri)) {
                    comprehensiveSchool = isTrue(type);
                } else if (CODE_OPPIAINEENKIELISYYS.equals(koodistoUri)) {
                    language = isTrue(type);
                }
            }
        }

        SubjectRow subjectRow = new SubjectRow(koodiArvo, translationsMap, optional, highSchool, comprehensiveSchool, language);
        subjectRow.toString();
        return subjectRow;
    }

    private boolean isTrue(final KoodiType koodiType) {
        return CODE_VALUE_TRUE.equals(koodiType.getKoodiArvo());
    }
}
