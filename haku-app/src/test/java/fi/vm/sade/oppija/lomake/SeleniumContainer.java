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
package fi.vm.sade.oppija.lomake;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.ui.selenium.SeleniumHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.TimeUnit;

/**
 * @author jukka
 * @version 10/19/123:28 PM}
 * @since 1.1
 */
public class SeleniumContainer implements DisposableBean {

    private SeleniumHelper seleniumHelper;

    public SeleniumContainer(TomcatContainer tomcatContainer) {
        WebDriver driver = new FirefoxDriver();
        Selenium selenium = new WebDriverBackedSelenium(driver, tomcatContainer.getBaseUrl());
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        this.seleniumHelper = new SeleniumHelper(selenium, driver, tomcatContainer.getBaseUrl());
    }

    public SeleniumHelper getSeleniumHelper() {
        return seleniumHelper;
    }

    @Override
    public void destroy() throws Exception {
        seleniumHelper.close();
    }
}
