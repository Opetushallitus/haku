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

import com.google.common.collect.ImmutableList;
import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;

public class DropdownSelectDefaultValueIT extends AbstractSeleniumBase {

    public static final String OPTION_1_ID = "fi";
    public static final String OPTION_2_ID = "sv";
    public static final String OPTION_3_ID = "xx";
    private final Option option1 = new Option(createI18NAsIs(OPTION_1_ID), OPTION_1_ID);
    private final Option option2 = new Option(createI18NAsIs(OPTION_2_ID), OPTION_2_ID);
    private final Option option3 = new Option(createI18NAsIs(OPTION_3_ID), OPTION_3_ID);
    private ApplicationSystemHelper applicationSystemHelper;
    private WebDriver driver;
    private DropdownSelect dropdownSelect;

    @Before
    public void init() throws IOException {
        String id = ElementUtil.randomId();
        dropdownSelect = new DropdownSelect(id, createI18NAsIs(id), null);
        option2.setDefaultOption(true);
        List<Option> listOfOptions = ImmutableList.of(option1, option2, option3);
        dropdownSelect.addOptions(listOfOptions);
        ApplicationSystem applicationSystem = new FormModelBuilder().buildDefaultFormWithFields(dropdownSelect);
        this.applicationSystemHelper = updateApplicationSystem(applicationSystem);
        driver = seleniumContainer.getDriver();
        driver.get(getBaseUrl() + this.applicationSystemHelper.getFormUrl(this.applicationSystemHelper.getFirstPhase().getId()));
    }

    @Test
    public void testSelect() throws IOException {
        WebElement we1 = findByXPath("//select['@name" +dropdownSelect.getId()+ "']/option[@value='" + option1.getValue()+ "']");
        WebElement we2 = findByXPath("//select['@name" +dropdownSelect.getId()+ "']/option[@value='" + option2.getValue()+ "']");
        WebElement we3 = findByXPath("//select['@name" +dropdownSelect.getId()+ "']/option[@value='" + option3.getValue()+ "']");
        assertEquals("Invalid attribute selected", null, we1.getAttribute("selected"));
        assertEquals("Invalid attribute selected", null, we3.getAttribute("selected"));
        assertEquals("Selected attribute not present on a selected option", "true", we2.getAttribute("selected"));
    }
}
