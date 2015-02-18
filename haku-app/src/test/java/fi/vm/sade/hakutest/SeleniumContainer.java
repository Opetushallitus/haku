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
package fi.vm.sade.hakutest;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Service
@Profile("it")
public class SeleniumContainer {

    private final FirefoxDriver webDriver;
    private final String webDriverBaseUrl;

    @Autowired
    public SeleniumContainer(@Value("${webdriver.base.url:http://localhost:9090/haku-app/}") final String webDriverBaseUrl) {
        this.webDriverBaseUrl = webDriverBaseUrl;
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("focusmanager.testmode",true);
        this.webDriver = new FirefoxDriver(profile);
        this.webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() throws Exception {
        webDriver.quit();
        webDriver.close();
    }

    public FirefoxDriver getDriver() {
        return webDriver;
    }

    public void logout() {
        webDriver.navigate().to(this.webDriverBaseUrl + "user/logout");
    }

    public String getBaseUrl() {
        return webDriverBaseUrl;
    }

    public void waitForAjax() {
        // explicit sleep is needed because bacon is grouping the request in 100ms intervals!
        try {
            Thread.sleep(101);
        } catch (InterruptedException e) { }

        (new WebDriverWait(webDriver, 5)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                JavascriptExecutor js = (JavascriptExecutor) d;
                return (Boolean) js.executeScript("return jQuery.active == 0");
            }
        });
    }
}
