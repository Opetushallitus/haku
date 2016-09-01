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

import fi.vm.sade.haku.oppija.lomake.domain.elements.custom.SubjectRow;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class KoodiTypeToSubjectRowFunctionTest {


    private KoodiTypeToSubjectRowFunction koodiTypeToSubjectRowFunction;
    private KoodiType koodiType;

    @Before
    public void setUp() throws Exception {
        koodiTypeToSubjectRowFunction = new KoodiTypeToSubjectRowFunction(null);
        this.koodiType = TestObjectCreator.createKoodiType(TestObjectCreator.KOODI_KOODI_URI_AND_ARVO);

    }

    @Test
    public void testApplyId() throws Exception {
        SubjectRow subjectRow = koodiTypeToSubjectRowFunction.apply(koodiType);
        assertEquals(TestObjectCreator.KOODI_KOODI_URI_AND_ARVO, subjectRow.getId());
    }

    @Test
    public void testApplyText() throws Exception {
        koodiType.getMetadata().add(TestObjectCreator.createKoodiMetadataType());
        SubjectRow subjectRow = koodiTypeToSubjectRowFunction.apply(koodiType);
        assertFalse(subjectRow.getI18nText().isEmpty());
    }
}
