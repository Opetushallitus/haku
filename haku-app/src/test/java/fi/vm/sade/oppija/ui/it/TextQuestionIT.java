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

package fi.vm.sade.oppija.ui.it;

import fi.vm.sade.oppija.common.it.AbstractFormTest;
import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import org.junit.Before;
import org.junit.Test;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NForm;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class TextQuestionIT extends AbstractFormTest {
    protected FormModel formModel;
    private FormModelHelper formModelHelper;

    @Before
    public void init() {
        TextQuestion textQuestion = new TextQuestion("sukunimi", createI18NForm("foo"));
        textQuestion.setHelp(createI18NForm("help"));
        formModel = new FormModelBuilder().buildDefaultFormWithFields(textQuestion);
        this.formModelHelper = updateModelAndCreateFormModelHelper(formModel);
    }

    @Test
    public void testFormExists() {
        beginAt(formModelHelper.getStartUrl());
        final String formId = formModelHelper.getFirstCategoryFormId();
        assertFormPresent(formId);
    }

    @Test
    public void testInputExists() {
        beginAt(formModelHelper.getStartUrl());
        assertElementPresent("sukunimi");
    }

    @Test
    public void testLabelExists() {
        beginAt(formModelHelper.getStartUrl());
        assertElementPresent("label-sukunimi");
    }

    @Test
    public void testHelpExists() {
        beginAt(formModelHelper.getStartUrl());
        assertElementPresent("help-sukunimi");
    }
}
