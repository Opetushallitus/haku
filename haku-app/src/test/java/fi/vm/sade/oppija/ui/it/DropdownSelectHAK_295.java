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
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

import static fi.vm.sade.oppija.lomake.domain.util.ElementUtil.createI18NAsIs;
import static org.junit.Assert.assertEquals;

public class DropdownSelectHAK_295 extends AbstractSeleniumBase {

    public static final String SELECT_ID = "select_id";
    public static final String OPTION_1_ID = "FI";
    public static final String OPTION_2_ID = "SV";
    public static final String OPTION_3_ID = "xx";
    public static final String FI_VM_SADE_OPPIJA_LANGUAGE = "fi_vm_sade_oppija_language";
    public static final String SELECTED_ATTRIBUTE = "selected";
    private FormModelHelper formModelHelper;
    private WebDriver driver;
    private DropdownSelect dropdownSelect;

    @Test
    public void testSelect() throws IOException {
        init(null, false);
        assertSelected(OPTION_1_ID);
        assertNotSelected(OPTION_2_ID);
        assertNotSelected(OPTION_3_ID);
    }

    @Test
    public void testSelectWithDefault() throws IOException {
        init(null, true);
        assertNotSelected(OPTION_1_ID);
        assertSelected(OPTION_2_ID);
        assertNotSelected(OPTION_3_ID);
    }

    @Test
    public void testSelectWithAttribute() throws IOException {
        init(FI_VM_SADE_OPPIJA_LANGUAGE, false);
        assertNotSelected(OPTION_2_ID);
        assertNotSelected(OPTION_3_ID);
        assertSelected(OPTION_1_ID);
        driver.get(getBaseUrl() + this.formModelHelper.getFormUrl(this.formModelHelper.getFirstCategory()) + "?lang=sv");
        assertNotSelected(OPTION_1_ID);
        assertNotSelected(OPTION_3_ID);
        assertSelected(OPTION_2_ID);
    }

    @Test
    public void testSelectWithAttributeAndDefault() throws IOException {
        init(FI_VM_SADE_OPPIJA_LANGUAGE, true);
        assertSelected(OPTION_1_ID);
        assertNotSelected(OPTION_2_ID);
        assertNotSelected(OPTION_3_ID);
    }

    private void init(final String dropdownAttribute, final boolean setDefault) {
        dropdownSelect = new DropdownSelect(SELECT_ID, createI18NAsIs(SELECT_ID), dropdownAttribute);
        Option option1 = new Option(OPTION_1_ID, createI18NAsIs(OPTION_1_ID), OPTION_1_ID);
        Option option2 = new Option(OPTION_2_ID, createI18NAsIs(OPTION_2_ID), OPTION_2_ID);
        Option option3 = new Option(OPTION_3_ID, createI18NAsIs(OPTION_3_ID), OPTION_3_ID);
        if (setDefault) {
            option2.setDefaultOption(true);
        }
        List<Option> listOfOptions = ImmutableList.of(option3, option2, option1);
        dropdownSelect.addOptions(listOfOptions);
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(dropdownSelect);
        this.formModelHelper = updateIndexAndFormModel(formModel);
        driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + this.formModelHelper.getFormUrl(this.formModelHelper.getFirstCategory()));
    }

    private void assertNotSelected(final String id) {
        WebElement element = driver.findElement(new By.ById(id));
        assertEquals("Invalid attribute selected (" + id + ")", null, element.getAttribute(SELECTED_ATTRIBUTE));
    }

    private void assertSelected(final String id) {
        WebElement element = driver.findElement(new By.ById(id));
        assertEquals("Selected attribute not found (" + id + ")", "true", element.getAttribute(SELECTED_ATTRIBUTE));
    }
}
