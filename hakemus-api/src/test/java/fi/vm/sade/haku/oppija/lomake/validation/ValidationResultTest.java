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

package fi.vm.sade.haku.oppija.lomake.validation;

import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public class ValidationResultTest {

    private ValidationResult validationResult;

    @Before
    public void setUp() throws Exception {
        this.validationResult = new ValidationResult();
    }

    @Test
    public void testHasErrorsFalse() throws Exception {
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testHasErrorsTrue() throws Exception {
        validationResult = new ValidationResult("key", ElementUtil.createI18NAsIs("error"));
        assertTrue(validationResult.hasErrors());
    }

    @Test(expected = NullPointerException.class)
    public void testHasErrorsNullMap() throws Exception {
        final Map<String, I18nText> errors = null;
        validationResult = new ValidationResult(errors);
        assertFalse(validationResult.hasErrors());
    }

    @Test(expected = NullPointerException.class)
    public void testHasErrorsNullList() throws Exception {
        final List<ValidationResult> validationResults = null;
        validationResult = new ValidationResult(validationResults);
        assertFalse(validationResult.hasErrors());
    }

    @Test
    public void testGetMessages() throws Exception {
        Map<String, I18nText> errorMessages = validationResult.getErrorMessages();
        assertEquals(0, errorMessages.size());
    }


}
