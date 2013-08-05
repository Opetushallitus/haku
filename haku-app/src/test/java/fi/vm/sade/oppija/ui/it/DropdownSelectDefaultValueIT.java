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
import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.lomake.domain.elements.questions.DropdownSelect;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Option;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

import static fi.vm.sade.oppija.lomakkeenhallinta.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;

public class DropdownSelectDefaultValueIT extends AbstractSeleniumBase {

    public static final String SELECT_ID = "select_id";
    public static final String OPTION_1_ID = "fi";
    public static final String OPTION_2_ID = "sv";
    public static final String OPTION_3_ID = "xx";
    private FormModelHelper formModelHelper;
    private WebDriver driver;
    private DropdownSelect dropdownSelect;

    @Before
    public void init() throws IOException {
        dropdownSelect = new DropdownSelect(SELECT_ID, createI18NAsIs(SELECT_ID), null);
        Option option1 = new Option(OPTION_1_ID, createI18NAsIs(OPTION_1_ID), OPTION_1_ID);
        Option option2 = new Option(OPTION_2_ID, createI18NAsIs(OPTION_2_ID), OPTION_2_ID);
        Option option3 = new Option(OPTION_3_ID, createI18NAsIs(OPTION_3_ID), OPTION_3_ID);
        option2.setDefaultOption(true);
        List<Option> listOfOptions = ImmutableList.of(option1, option2, option3);
        dropdownSelect.addOptions(listOfOptions);
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(dropdownSelect);
        this.formModelHelper = updateIndexAndFormModel(formModel);
        driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + this.formModelHelper.getFormUrl(this.formModelHelper.getFirstPhase().getId()));
    }

    @Test
    public void testSelect() throws IOException {
        WebElement option1 = driver.findElement(new By.ById(OPTION_1_ID));
        WebElement option2 = driver.findElement(new By.ById(OPTION_2_ID));
        WebElement option3 = driver.findElement(new By.ById(OPTION_3_ID));
        assertEquals("Invalid attribute selected", null, option1.getAttribute("selected"));
        assertEquals("Invalid attribute selected", null, option3.getAttribute("selected"));
        assertEquals("Selected attribute not present on a selected option", "true", option2.getAttribute("selected"));
    }
}
