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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KoodistoServiceMockImplTest {

    private KoodistoServiceMockImpl koodistoServiceMock;

    @Before
    public void setUp() throws Exception {
        koodistoServiceMock = new KoodistoServiceMockImpl();
    }

    @Test
    public void testGetPostOffices() throws Exception {
        assertEquals(koodistoServiceMock.listOfPostOffices, koodistoServiceMock.getPostOffices());
    }

    @Test
    public void testGetGradeRanges() throws Exception {
        assertEquals(koodistoServiceMock.listOfGradeGrades, koodistoServiceMock.getGradeRanges());
    }

    @Test
    public void testGetLearningInstitutionTypes() throws Exception {
        assertEquals(koodistoServiceMock.listOfLearningInstitutionTypes,
                koodistoServiceMock.getLearningInstitutionTypes());
    }

    @Test
    public void testGetBaseEductionCodes() {
        assertEquals(koodistoServiceMock.listOfBaseEducationCodes,
                koodistoServiceMock.getCodes(KoodistoServiceMockImpl.BASE_EDUCATION_KOODISTO_URI, 1));
    }
}
