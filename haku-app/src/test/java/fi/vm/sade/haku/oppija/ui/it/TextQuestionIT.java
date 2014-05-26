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

package fi.vm.sade.haku.oppija.ui.it;

import fi.vm.sade.haku.oppija.common.it.AbstractFormTest;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.TextQuestion;
import fi.vm.sade.haku.oppija.lomake.domain.builder.TextQuestionBuilder;
import org.junit.Before;
import org.junit.Test;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class TextQuestionIT extends AbstractFormTest {
    private ApplicationSystemHelper applicationSystemHelper;

    @Before
    public void init() {
        TextQuestion textQuestion = (TextQuestion) new TextQuestionBuilder("sukunimi").i18nText(createI18NAsIs("foo")).build();
        textQuestion.setHelp(createI18NAsIs("help"));
        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(textQuestion);
        this.applicationSystemHelper = updateModelAndCreateFormModelHelper(applicationSystem);
    }

    @Test
    public void testFormExists() {
        String startUrl = applicationSystemHelper.getStartUrl();
        beginAt(startUrl);
        assertFormPresent("form-" + applicationSystemHelper.getFirstPhase().getId()); //check phase.jsp html form id
    }

    @Test
    public void testInputExists() {
        beginAt(applicationSystemHelper.getStartUrl());
        assertElementPresent("sukunimi");
    }

    @Test
    public void testLabelExists() {
        beginAt(applicationSystemHelper.getStartUrl());
        assertElementPresent("label-sukunimi");
    }

    @Test
    public void testHelpExists() {
        beginAt(applicationSystemHelper.getStartUrl());
        assertElementPresent("help-sukunimi");
    }
}
