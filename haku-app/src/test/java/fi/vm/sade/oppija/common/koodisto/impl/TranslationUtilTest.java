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

package fi.vm.sade.oppija.common.koodisto.impl;

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
    public void testCreateTranslationsMap() throws Exception {
        KoodiMetadataType koodiMetadataType = TestObjectCreator.createKoodiMetadataType();
        koodiType.getMetadata().add(koodiMetadataType);
        Map<String, String> translationsMap = TranslationsUtil.createTranslationsMap(koodiType);
        String value = translationsMap.get(KieliType.FI.value().toLowerCase());
        assertEquals(TestObjectCreator.NIMI, value);
    }

    @Test
    public void testEmptyMetadata() throws Exception {
        Map<String, String> translationsMap = TranslationsUtil.createTranslationsMap(koodiType);
        assertTrue(translationsMap.isEmpty());
    }
}
