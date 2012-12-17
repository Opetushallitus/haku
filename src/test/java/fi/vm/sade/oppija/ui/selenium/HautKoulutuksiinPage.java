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

package fi.vm.sade.oppija.ui.selenium;

import fi.vm.sade.oppija.common.selenium.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author jukka
 * @version 10/15/124:00 PM}
 * @since 1.1
 */
public class HautKoulutuksiinPage extends LoginPage implements PageObject {

    private final String baseUrl;
    private final SeleniumHelper seleniumHelper;

    public HautKoulutuksiinPage(String baseUrl, SeleniumHelper seleniumHelper) {
        super(seleniumHelper.getSelenium());
        this.baseUrl = baseUrl;
        this.seleniumHelper = seleniumHelper;
    }

    protected void login() {
        seleniumHelper.getDriver().get(baseUrl);
        final WebElement element = seleniumHelper.getDriver().findElement(By.linkText("Kirjaudu sisään"));
        element.click();
        login("test");
    }

    @Override
    public String getUrl() {
        return baseUrl + "/oma/applications";
    }

}
