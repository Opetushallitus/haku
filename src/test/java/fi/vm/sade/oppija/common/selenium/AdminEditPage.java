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

package fi.vm.sade.oppija.common.selenium;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.lomake.converter.FormModelToJsonString;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.ui.selenium.PageObject;
import fi.vm.sade.oppija.ui.selenium.SeleniumHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author jukka
 * @version 10/15/123:47 PM}
 * @since 1.1
 */
public class AdminEditPage extends LoginPage implements PageObject {
    private final String baseUrl;
    private final Selenium selenium;
    private WebDriver driver;

    public AdminEditPage(String baseUrl, SeleniumHelper selenium) {
        super(selenium.getSelenium());
        this.baseUrl = baseUrl;
        this.selenium = selenium.getSelenium();
        this.driver = selenium.getDriver();
    }


    @Override
    public String getUrl() {
        return baseUrl + "/admin/edit";
    }


    public void submitForm(FormModel formModel1) {
        String convert = new FormModelToJsonString().apply(formModel1);
        convert = convert.replaceAll("\\\\", "\\\\\\\\");
        final WebElement model = driver.findElement(By.id("model"));
        model.clear();
        selenium.runScript("document.getElementById('model').value='" + convert + "'");
        // model.sendKeys(convert);
        final WebElement tallenna = driver.findElement(By.id("tallenna"));
        tallenna.click();
    }
}
