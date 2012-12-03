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

import com.thoughtworks.selenium.Selenium;
import org.openqa.selenium.WebDriver;

/**
 * @author jukka
 * @version 10/15/123:41 PM}
 * @since 1.1
 */
public class SeleniumHelper {
    private final Selenium selenium;
    private final WebDriver driver;
    private final String baseUrl;

    public SeleniumHelper(Selenium selenium, WebDriver driver, String baseUrl) {
        this.selenium = selenium;
        this.driver = driver;
        this.baseUrl = baseUrl;
    }

    protected void login(String user) {
        selenium.type("j_username", user);
        selenium.type("j_password", user);
        selenium.submit("login");
    }

    public void navigate(PageObject pageObject) {
        driver.get(pageObject.getUrl());
    }

    public Selenium getSelenium() {
        return selenium;
    }

    public void close() {
        driver.close();
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void logout() {
        getDriver().get(baseUrl + "/logout");
    }
}
