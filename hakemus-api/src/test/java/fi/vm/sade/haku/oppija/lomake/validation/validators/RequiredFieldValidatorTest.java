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

package fi.vm.sade.haku.oppija.lomake.validation.validators;

import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.util.SpringInjector;
import fi.vm.sade.haku.oppija.lomake.validation.ValidationInput;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RequiredFieldValidatorTest {
    public static final String ERROR_MESSAGE_KEY = "error.message.key";
    public static final String FIELD_NAME = "kenttä";
    private static final Element element = new TextQuestion(FIELD_NAME, ElementUtil.createI18NAsIs(FIELD_NAME));
    private Map<String, String> values;
    private RequiredFieldValidator requiredFieldValidator;


    @Before
    public void setUp() throws Exception {
        SpringInjector.setTestMode(true);
        values = new HashMap<String, String>();
        requiredFieldValidator = new RequiredFieldValidator(FIELD_NAME, ERROR_MESSAGE_KEY);
    }

    @Test
    public void testValidateValid() throws Exception {
        values.put(FIELD_NAME, "1");
        assertFalse(isValid());
    }

    @Test
    public void testValidateInvalid() throws Exception {
        assertTrue(isValid());
    }

    private boolean isValid() {
        return requiredFieldValidator.validate(new ValidationInput(element, values, null, "", ValidationInput.ValidationContext.officer_modify)).hasErrors();
    }

}
