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

import fi.vm.sade.oppija.common.MongoWrapper;
import fi.vm.sade.oppija.common.it.AdminResourceClient;
import fi.vm.sade.oppija.common.it.TomcatContainerBase;
import fi.vm.sade.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.oppija.lomake.SeleniumContainer;
import fi.vm.sade.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.oppija.ui.selenium.SeleniumHelper;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSeleniumBase extends TomcatContainerBase {

    protected SeleniumHelper seleniumHelper;

    @Autowired
    SeleniumContainer container;

    @Autowired
    protected MongoWrapper mongoWrapper;

    @Before
    public void before() {
        mongoWrapper.dropDatabase();
        seleniumHelper = container.getSeleniumHelper();
        seleniumHelper.logout();
    }

    @After
    public void tearDown() throws Exception {
        mongoWrapper.dropDatabase();
    }

    protected ApplicationSystemHelper updateApplicationSystem(final ApplicationSystem applicationSystem) {
        adminResourceClient.updateApplicationSystem(applicationSystem);
        return new ApplicationSystemHelper(applicationSystem);
    }

    protected void screenshot(String filename) {
        boolean debug = Boolean.parseBoolean(System.getProperty("debugTests", "false"));
        if (!debug) {
            return;
        }
        WebDriver driver = seleniumHelper.getDriver();
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File("target/" + filename + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void findByIdAndClick(final String... ids) {
        WebDriver driver = seleniumHelper.getDriver();
        for (String id : ids) {
            driver.findElement(new By.ById(id)).click();
        }
    }

    protected void selectByValue(final String id, final String value) {
        Select select = new Select(seleniumHelper.getDriver().findElement(new By.ById(id)));
        select.selectByValue(value);
    }

    protected void findById(final String... ids) {
        WebDriver driver = seleniumHelper.getDriver();
        for (String id : ids) {
            driver.findElement(new By.ById(id));
        }
    }

    protected void findByXPath(final String xpath) {
        seleniumHelper.getDriver().findElement(By.xpath(xpath));
    }

    protected List<WebElement> findByClassName(final String... classNames) {
        List<WebElement> elements = new ArrayList<WebElement>();
        for (String className : classNames) {
            elements.addAll(seleniumHelper.getDriver().findElements(new By.ByClassName(className)));
        }
        return elements;
    }

}
