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

package fi.vm.sade.oppija.haku.validation;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.elements.Element;
import fi.vm.sade.oppija.haku.domain.elements.questions.TextQuestion;
import fi.vm.sade.oppija.haku.domain.rules.RelatedQuestionRule;
import fi.vm.sade.oppija.haku.service.FormModelInitializer;
import fi.vm.sade.oppija.haku.validation.validators.ConditionalFieldValidator;
import fi.vm.sade.oppija.haku.validation.validators.RequiredFieldFieldValidator;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author jukka
 * @version 11/12/1210:29 AM}
 * @since 1.1
 */
public class ValidationInitTest {


    @Test
    public void testCollector() throws Exception {
        final FormModel formModel = createModelWithOneRequiredField();
        final List<Validator> validators = getValidators(formModel, FormModelBuilder.VAIHE_ID);
        assertEquals(1, validators.size());
    }

    private List<Validator> getValidators(FormModel formModel, String vaiheId) {
        final FormModelHelper formModelHelper = new FormModelHelper(formModel);
        return formModelHelper.getFirstForm().getCategory(vaiheId).getValidators();
    }

    @Test
    public void testCollectorWithConditionalValidators() throws Exception {
        final FormModel formModel = createModelWithConditionalField();
        final List<Validator> validators = getValidators(formModel, FormModelBuilder.VAIHE_ID);
        assertEquals(1, validators.size());
        assertEquals(ConditionalFieldValidator.class, validators.get(0).getClass());
    }

    @Test
    public void testCollectorWithMultipleConditionalValidators() throws Exception {
        final FormModel formModel = createModelWithTwoConditionalFields();
        final List<Validator> validators = getValidators(formModel, FormModelBuilder.VAIHE_ID);
        assertEquals(3, validators.size());
        assertEquals(ConditionalFieldValidator.class, validators.get(0).getClass());
    }

    @Test
    public void testDummyModel() throws Exception {
        final FormModel model = new FormModelDummyMemoryDaoImpl().getModel();
        new FormModelInitializer(model).initModel();
        final List<Validator> validators = getValidators(model, "koulutustausta");
        assertEquals(3, validators.size());
        assertEquals(ConditionalFieldValidator.class, validators.get(0).getClass());
        assertEquals(RequiredFieldFieldValidator.class, validators.get(1).getClass());
    }


    private FormModel createModelWithTwoConditionalFields() {
        final RelatedQuestionRule rule = createRule("rule");

        final RelatedQuestionRule rule2 = createRule("rule2");

        final RelatedQuestionRule rule3 = new RelatedQuestionRule("rule3", "rule", ".*");
        rule3.addChild(createRequiredTextField());

        return createModel(rule, rule2, rule3);
    }

    private FormModel createModelWithConditionalField() {
        final RelatedQuestionRule rule = createRule("rule");
        return createModel(rule);
    }

    private RelatedQuestionRule createRule(String id) {
        final RelatedQuestionRule rule = new RelatedQuestionRule(id, "rule", ".*");
        rule.addChild(createRequiredTextField());
        return rule;
    }


    private FormModel createModelWithOneRequiredField() {
        return createModel(createRequiredTextField());
    }

    private FormModel createModel(Element... field) {
        final FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(field);
        new FormModelInitializer(formModel).initModel();
        return formModel;
    }

    private TextQuestion createRequiredTextField() {
        final TextQuestion textQuestion = new TextQuestion("id" + System.currentTimeMillis(), "title" + System.currentTimeMillis());
        textQuestion.addAttribute("required", "true");
        return textQuestion;
    }
}
