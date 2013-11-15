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
import fi.vm.sade.oppija.lomake.validation.ValidationInput;
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
        Predicate<ValidationInput> predicate = and(validate(
                new RegexFieldValidator("a", ElementUtil.createI18NText("yleinen.virheellinenArvo",
                        "form_errors_yhteishaku_syksy"), "foo")),
                validate(new RegexFieldValidator(
                        "b", ElementUtil.createI18NText("yleinen.virheellinenArvo",
                        "form_errors_yhteishaku_syksy"), "bar")));

        FunctionalValidator fv = new FunctionalValidator(predicate, "id", ElementUtil.createI18NAsIs("error"));
        Map<String, String> values = new HashMap<String, String>();
        values.put("a", "foo");
        values.put("b", "bar");
        ValidationResult result = fv.validate(new ValidationInput(null, values, null, null));
        assertNotNull(result);
        assertFalse(result.hasErrors());
    }

    @Test
    public void testInvalidAndOperator() {
        Predicate<ValidationInput> predicate = and(validate(
                new RegexFieldValidator("a", ElementUtil.createI18NText("yleinen.virheellinenArvo",
                        "form_errors_yhteishaku_syksy"), "foo")),
                validate(new RegexFieldValidator(
                        "b", ElementUtil.createI18NText("yleinen.virheellinenArvo",
                        "form_errors_yhteishaku_syksy"), "bar")));

        FunctionalValidator fv = new FunctionalValidator(predicate, "id", ElementUtil.createI18NAsIs("error"));
        Map<String, String> values = new HashMap<String, String>();
        values.put("a", "foo");
        values.put("b", "fail");
        ValidationResult result = fv.validate(new ValidationInput(null, values, null, null));
        assertNotNull(result);
        assertTrue(result.hasErrors());
    }

    @Test
    public void testValidAndOrOperators() {
        Predicate<ValidationInput> predicate =
                or(
                        and(
                                validate(ElementUtil.createRegexValidator("a", "foo", "form_errors_yhteishaku_syksy")),
                                validate(ElementUtil.createRegexValidator("b", "bar", "form_errors_yhteishaku_syksy"))),
                        validate(ElementUtil.createRegexValidator("c", "ok", "form_errors_yhteishaku_syksy")));

        FunctionalValidator fv = new FunctionalValidator(predicate, "id", ElementUtil.createI18NAsIs("error"));
        Map<String, String> values = new HashMap<String, String>();
        values.put("a", "foo");
        values.put("b", "fail");
        values.put("c", "ok");
        ValidationResult result = fv.validate(new ValidationInput(null, values, null, null));
        assertNotNull(result);
        assertFalse(result.hasErrors());
    }

    @Test
    public void testValidAndOperatorWithNegation() {
        Predicate<ValidationInput> predicate = and(validate(
                new RegexFieldValidator("a",
                        ElementUtil.createI18NText("yleinen.virheellinenArvo", "form_errors_yhteishaku_syksy"), "foo")),
                not(validate(new RegexFieldValidator("b",
                        ElementUtil.createI18NText("yleinen.virheellinenArvo", "form_errors_yhteishaku_syksy"), "bar"))));

        FunctionalValidator fv = new FunctionalValidator(predicate, "id", ElementUtil.createI18NAsIs("error"));
        Map<String, String> values = new HashMap<String, String>();
        values.put("a", "foo");
        values.put("b", "fail");
        ValidationResult result = fv.validate(new ValidationInput(null, values, null, null));
        assertNotNull(result);
        assertFalse(result.hasErrors());
    }
}
