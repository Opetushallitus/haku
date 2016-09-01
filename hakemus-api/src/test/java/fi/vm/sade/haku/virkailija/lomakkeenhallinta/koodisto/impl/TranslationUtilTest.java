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

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TranslationUtilTest {


    private KoodiType koodiType;

    @Before
    public void setUp() throws Exception {
        this.koodiType = new KoodiType();
    }

    @Test
    public void testCreateTranslationsMapNoTranslations() throws Exception {
        KoodiMetadataType koodiMetadataType = TestObjectCreator.createKoodiMetadataType();
        koodiType.getMetadata().add(koodiMetadataType);
        I18nText translationsMap = TranslationsUtil.createTranslationsMap(koodiType);
        String value_fi = translationsMap.getText(KieliType.FI.value().toLowerCase());
        assertEquals(TestObjectCreator.NIMI, value_fi);
        String value_sv = translationsMap.getText(KieliType.SV.value().toLowerCase());
        assertEquals(TestObjectCreator.NIMI, value_sv);
        String value_en = translationsMap.getText(KieliType.EN.value().toLowerCase());
        assertEquals(TestObjectCreator.NIMI, value_en);
    }

    @Test
    public void testCreateTranslationsMap() throws Exception {
        KoodiMetadataType koodiMetadataType = TestObjectCreator.createKoodiMetadataType();
        KoodiMetadataType koodiMetadataType2 = TestObjectCreator.createKoodiMetadataType2();
        koodiType.getMetadata().add(koodiMetadataType);
        koodiType.getMetadata().add(koodiMetadataType2);
        I18nText translationsMap = TranslationsUtil.createTranslationsMap(koodiType);
        String value_fi = translationsMap.getText(KieliType.FI.value().toLowerCase());
        assertEquals(TestObjectCreator.NIMI, value_fi);
        String value_sv = translationsMap.getText(KieliType.SV.value().toLowerCase());
        assertEquals(TestObjectCreator.NIMI_2, value_sv);
        String value_en = translationsMap.getText(KieliType.EN.value().toLowerCase());
        assertEquals(TestObjectCreator.NIMI, value_en);
    }

    @Test
    public void testEmptyMetadata() throws Exception {
        I18nText translationsMap = TranslationsUtil.createTranslationsMap(koodiType);
        assertTrue(translationsMap.isEmpty());
    }
}
