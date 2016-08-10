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

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
@Profile("it")
public class SeleniumContainer {

    private RemoteWebDriver webDriver;
    private final String webDriverBaseUrl;

    @Autowired
    public SeleniumContainer(@Value("${webdriver.base.url:http://localhost:9090/haku-app/}") final String webDriverBaseUrl) {
        this.webDriverBaseUrl = webDriverBaseUrl;
    }

    @PreDestroy
    public void destroy() throws Exception {
        getDriver().quit();
        getDriver().close();
    }

    public RemoteWebDriver getDriver() {
        if (webDriver == null) {
            if(Boolean.getBoolean("it.usePhantomJs")) {
                PhantomJsDriverManager.getInstance().setup();
                this.webDriver = new PhantomJSDriver();
            } else {
                ChromeDriverManager.getInstance().setup();
                this.webDriver = new ChromeDriver();
            }
        }
        return webDriver;
    }

    public void logout() {
        getDriver().navigate().to(this.webDriverBaseUrl + "user/logout");
    }

    public String getBaseUrl() {
        return webDriverBaseUrl;
    }
}
