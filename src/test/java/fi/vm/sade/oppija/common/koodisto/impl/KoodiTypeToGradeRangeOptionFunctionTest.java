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

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class KoodiTypeToGradeRangeOptionFunctionTest {

    private KoodiTypeToGradeRangeOptionFunction koodiTypeToGradeRangeOptionFunction;
    private KoodiType koodiType;

    @Before
    public void setUp() throws Exception {
        koodiTypeToGradeRangeOptionFunction = new KoodiTypeToGradeRangeOptionFunction();
        this.koodiType = TestObjectCreator.createKoodiType(TestObjectCreator.KOODI_ARVO);

    }

    @Test
    public void testApplyId() throws Exception {
        Option option = koodiTypeToGradeRangeOptionFunction.apply(koodiType);
        assertEquals(KoodiTypeToGradeRangeOptionFunction.ID_PREFIX +
                TestObjectCreator.KOODI_ARVO, option.getId());
    }

    @Test
    public void testApplyText() throws Exception {
        koodiType.getMetadata().add(TestObjectCreator.createKoodiMetadataType());
        Option option = koodiTypeToGradeRangeOptionFunction.apply(koodiType);
        assertFalse(option.getI18nText().getTranslations().isEmpty());
    }

    @Test
    public void testApplyValue() throws Exception {
        Option option = koodiTypeToGradeRangeOptionFunction.apply(koodiType);
        assertEquals(TestObjectCreator.KOODI_ARVO, option.getValue());
    }
}
