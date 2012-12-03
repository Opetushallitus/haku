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

package fi.vm.sade.oppija.lomake.validation.validators;

import fi.vm.sade.oppija.lomake.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomake.validation.Validator;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ConditionalFieldValidatorTest {

    public static final ConditionalFieldValidator CONDITIONAL_FIELD_VALIDATOR =
            new ConditionalFieldValidator(new RelatedQuestionRule("id", "relatedId", "expr"));
    public static final Validator VALIDATOR = new Validator() {
        @Override
        public ValidationResult validate(Map<String, String> values) {
            return null;
        }
    };

    @Test
    public void testAdd() throws Exception {
        ConditionalFieldValidator conditionalFieldValidator = CONDITIONAL_FIELD_VALIDATOR;
        conditionalFieldValidator.add(VALIDATOR);
        assertTrue(conditionalFieldValidator.validators.contains(VALIDATOR));
    }

    @Test
    public void testValidate() throws Exception {
        ValidationResult validate = CONDITIONAL_FIELD_VALIDATOR.validate(new HashMap<String, String>(0));
        assertTrue(!validate.hasErrors());
    }
}
