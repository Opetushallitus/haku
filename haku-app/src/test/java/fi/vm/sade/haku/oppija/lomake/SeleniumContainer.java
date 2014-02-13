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
package fi.vm.sade.haku.oppija.lomake;

import com.thoughtworks.selenium.Selenium;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Service
@Profile("it")
public class SeleniumContainer {

    private final WebDriver webDriver;
    private final Selenium selenium;
    private final String webDriverBaseUrl;

    @Autowired
    public SeleniumContainer(@Value("${webdriver.base.url:http://localhost:9090/haku-app/}") final String webDriverBaseUrl) {
        this.webDriverBaseUrl = webDriverBaseUrl;
        this.webDriver = new FirefoxDriver();
        this.selenium = new WebDriverBackedSelenium(this.webDriver, this.webDriverBaseUrl);
        this.webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() throws Exception {
        webDriver.quit();
        webDriver.close();
        selenium.close();
    }

    public WebDriver getDriver() {
        return webDriver;
    }

    public void logout() {
        selenium.open(this.webDriverBaseUrl + "user/logout");
    }

    public String getBaseUrl() {
        return webDriverBaseUrl;
    }

    public void navigate(final String path) {
        selenium.open(webDriverBaseUrl + path);
    }

    public Selenium getSelenium() {
        return selenium;
    }
}
