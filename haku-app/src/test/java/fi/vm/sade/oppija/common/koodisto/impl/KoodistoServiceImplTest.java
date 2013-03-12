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

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KoodistoServiceImplTest {

    private KoodistoServiceImpl koodistoService;

    @Before
    public void setUp() throws Exception {
        KoodiService koodiService = mock(KoodiService.class);
        when(koodiService.searchKoodisByKoodisto(any(SearchKoodisByKoodistoCriteriaType.class))).thenReturn(TestObjectCreator.createKoodiTypeList());
        koodistoService = new KoodistoServiceImpl(koodiService);
    }

    @Test
    public void testGetPostOffices() throws Exception {
        assertFalse(koodistoService.getPostOffices().isEmpty());
    }

    @Test
    public void testGetSubjects() throws Exception {
        assertFalse(koodistoService.getSubjects().isEmpty());
    }

    @Test
    public void testGetGradeRanges() throws Exception {
        assertFalse(koodistoService.getGradeRanges().isEmpty());
    }

    @Test
    public void testGetOrganizationTypes() throws Exception {
        assertFalse(koodistoService.getOrganizationtypes().isEmpty());
    }

    @Test
    public void testGetLearningInstitutionTypes() throws Exception {
        assertFalse(koodistoService.getLearningInstitutionTypes().isEmpty());
    }

    @Test
    public void testGetCountries() throws Exception {
        assertFalse(koodistoService.getCountries().isEmpty());
    }

    @Test
    public void testGetNationalities() throws Exception {
        assertFalse(koodistoService.getNationalities().isEmpty());
    }

    @Test
    public void testGetLanguages() throws Exception {
        assertFalse(koodistoService.getLanguages().isEmpty());
    }

    @Test
    public void testGetMunicipalities() throws Exception {
        assertFalse(koodistoService.getMunicipalities().isEmpty());
    }
}
