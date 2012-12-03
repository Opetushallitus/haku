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

package fi.vm.sade.oppija.lomake.selenium;

import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.lomake.domain.elements.questions.TextQuestion;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author jukka
 * @version 10/15/123:25 PM}
 * @since 1.1
 */
public class HautKoulutuksiinTest extends AbstractSeleniumBase {

    private FormModelHelper formModelHelper;

    @Test
    public void testSaveHakemusAndList() {
        buildFormWithOneQuestion();
        loginAsNormalUser();
        fillForm();
        loginAsNormalUser();
        assertTrue(weAreAtAjankohtaisetHakemukset());
        assertTrue(hakemusListIncludesFilledForm());
    }

    private boolean hakemusListIncludesFilledForm() {
        final String title = formModelHelper.getFirstForm().getTitle();
        return seleniumHelper.getSelenium().isTextPresent(title);
    }

    private boolean weAreAtAjankohtaisetHakemukset() {
        return seleniumHelper.getSelenium().isTextPresent("Ajankohtaiset hakemukset");
    }

    private void loginAsNormalUser() {
        seleniumHelper.logout();
        new HautKoulutuksiinPage(getBaseUrl(), seleniumHelper).login();
    }

    private void fillForm() {
        seleniumHelper.getDriver().get(getBaseUrl() + "/" + formModelHelper.getStartUrl());
        seleniumHelper.getSelenium().type("eka", "arvo");
        seleniumHelper.getSelenium().click("class=right");
    }

    private void buildFormWithOneQuestion() {
        final FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(new TextQuestion("eka", "kysymys"));
        formModelHelper = initModel(formModel);
    }
}
