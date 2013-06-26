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

import com.google.common.base.Predicate;
import fi.vm.sade.oppija.lomake.validation.ValidationResult;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Predicates.*;
import static fi.vm.sade.oppija.lomake.validation.validators.FunctionalValidator.ValidatorPredicate.validate;
import static org.junit.Assert.*;

/**
 * @author Mikko Majapuro
 */
public class FunctionalValidatorTest {

    @Test
    public void testValidAndOperator() {
        Predicate<Map<String, String>> predicate = and(validate(
                new RegexFieldValidator("a", ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), "foo")),
                validate(new RegexFieldValidator(
                        "b", ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), "bar")));

        FunctionalValidator fv = new FunctionalValidator(predicate, "id", ElementUtil.createI18NAsIs("error"));
        Map<String, String> values = new HashMap<String, String>();
        values.put("a", "foo");
        values.put("b", "bar");
        ValidationResult result = fv.validate(values);
        assertNotNull(result);
        assertFalse(result.hasErrors());
    }

    @Test
    public void testInvalidAndOperator() {
        Predicate<Map<String, String>> predicate = and(validate(
                new RegexFieldValidator("a", ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), "foo")),
                validate(new RegexFieldValidator(
                        "b", ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), "bar")));

        FunctionalValidator fv = new FunctionalValidator(predicate, "id", ElementUtil.createI18NAsIs("error"));
        Map<String, String> values = new HashMap<String, String>();
        values.put("a", "foo");
        values.put("b", "fail");
        ValidationResult result = fv.validate(values);
        assertNotNull(result);
        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidAndOrOperators() {
        Predicate<Map<String, String>> predicate = or(and(validate(new RegexFieldValidator("a",
                ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), "foo")),
                validate(new RegexFieldValidator(
                        "b", ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), "bar"))),
                validate(new RegexFieldValidator(
                        "c", ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), "ok")));

        FunctionalValidator fv = new FunctionalValidator(predicate, "id", ElementUtil.createI18NAsIs("error"));
        Map<String, String> values = new HashMap<String, String>();
        values.put("a", "foo");
        values.put("b", "fail");
        values.put("c", "ok");
        ValidationResult result = fv.validate(values);
        assertNotNull(result);
        assertFalse(result.hasErrors());
    }

    @Test
    public void testValidAndOperatorWithNegation() {
        Predicate<Map<String, String>> predicate = and(validate(
                new RegexFieldValidator("a",
                        ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), "foo")),
                not(validate(new RegexFieldValidator("b",
                        ElementUtil.createI18NTextError("yleinen.virheellinenArvo"), "bar"))));

        FunctionalValidator fv = new FunctionalValidator(predicate, "id", ElementUtil.createI18NAsIs("error"));
        Map<String, String> values = new HashMap<String, String>();
        values.put("a", "foo");
        values.put("b", "fail");
        ValidationResult result = fv.validate(values);
        assertNotNull(result);
        assertFalse(result.hasErrors());
    }
}
