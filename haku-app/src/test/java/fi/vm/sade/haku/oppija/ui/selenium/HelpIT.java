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

package fi.vm.sade.haku.oppija.ui.selenium;


import fi.vm.sade.haku.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.I18nText;
import fi.vm.sade.haku.oppija.lomake.domain.builder.CheckBoxBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Element;
import fi.vm.sade.haku.oppija.lomake.domain.elements.questions.CheckBox;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;

public class HelpIT extends AbstractSeleniumBase {

    public static final String ID = "ID";
    public static final String HELP_DIV_PREFIX = "help-";
    public static final String HELP_ID = HELP_DIV_PREFIX + ID;
    public static final String TITLE = "TITLE";
    public static final I18nText HELP_TEXT = createI18NAsIs("Apuva");

    private ApplicationSystemHelper applicationSystemHelper;
    private CheckBox checkBox;

    @Before
    public void beforeHelpIt() throws Exception {
        checkBox = (CheckBox) CheckBoxBuilder.Checkbox(ID).i18nText(createI18NAsIs(TITLE)).build();
    }

    @Test
    public void testCheckBox() {
        checkBox.setHelp(HELP_TEXT);
        String actualHelpText = initModelAndGetHelpText(checkBox);
        assertEquals(HELP_TEXT.getTranslations().get("fi"), actualHelpText);
    }

    @Test(expected = org.openqa.selenium.NoSuchElementException.class)
    public void testCheckBoxWithoutHelp() {
        initModelAndGetHelpText(checkBox);
    }

    private String initModelAndGetHelpText(final Element element) {
        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(element);
        this.applicationSystemHelper = updateApplicationSystem(applicationSystem);
        this.seleniumContainer.getDriver().get(getBaseUrl() + applicationSystemHelper.getStartUrl());
        WebElement helpWebElement = seleniumContainer.getDriver().findElement(By.id(HELP_ID));
        return helpWebElement.getText();
    }
}
