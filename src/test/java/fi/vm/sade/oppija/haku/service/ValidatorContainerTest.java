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

package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.haku.validation.Validator;
import fi.vm.sade.oppija.haku.validation.validators.RegexFieldValidator;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ValidatorContainerTest {

    public static final RegexFieldValidator TEST_VALIDATOR = new RegexFieldValidator("test", "test");
    public static final String TEST_CATEGORY = "test";

    @Test
    public void testGetCategoryValidators() throws Exception {
        ValidatorContainer validatorContainer = new ValidatorContainer();
        validatorContainer.addValidator(TEST_CATEGORY, TEST_VALIDATOR);
        List<Validator> categoryValidators = validatorContainer.getCategoryValidators(TEST_CATEGORY);
        assertEquals(1, categoryValidators.size());
    }
    @Test
    public void testGetCategoryValidatorsEmpty() throws Exception {
        ValidatorContainer validatorContainer = new ValidatorContainer();
        List<Validator> categoryValidators = validatorContainer.getCategoryValidators(TEST_CATEGORY);
        assertEquals(0, categoryValidators.size());
    }
    @Test
    public void testGetCategoryValidatorsNullCategory() throws Exception {
        ValidatorContainer validatorContainer = new ValidatorContainer();
        List<Validator> categoryValidators = validatorContainer.getCategoryValidators(null);
        assertEquals(0, categoryValidators.size());
    }
}
