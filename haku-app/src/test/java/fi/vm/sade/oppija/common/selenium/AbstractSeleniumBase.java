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

import fi.vm.sade.oppija.common.it.AdminResourceClient;
import fi.vm.sade.oppija.common.it.TomcatContainerBase;
import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.SeleniumContainer;
import fi.vm.sade.oppija.lomake.dao.TestDBFactoryBean;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.ui.selenium.SeleniumHelper;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

public abstract class AbstractSeleniumBase extends TomcatContainerBase {

    protected SeleniumHelper seleniumHelper;

    @Autowired
    SeleniumContainer container;

    @Autowired
    TestDBFactoryBean dbFactory;

    public AbstractSeleniumBase() {
        super();
    }

    @Before
    public void before() {
        dbFactory.drop();
        seleniumHelper = container.getSeleniumHelper();
        seleniumHelper.logout();
    }

    protected FormModelHelper updateIndexAndFormModel(FormModel formModel) {
        AdminResourceClient adminResourceClient = new AdminResourceClient(getBaseUrl());
        adminResourceClient.updateIndex();
        adminResourceClient.updateModel(formModel);
        return new FormModelHelper(formModel);
    }

    protected void updateModel(FormModel formModel) {
        AdminResourceClient adminResourceClient = new AdminResourceClient(getBaseUrl());
        adminResourceClient.updateModel(formModel);
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

    protected void findById(final String... ids) {
        WebDriver driver = seleniumHelper.getDriver();
        for (String id : ids) {
            driver.findElement(new By.ById(id));
        }
    }

    protected void findByXPath(final String xpath) {
        seleniumHelper.getDriver().findElement(By.xpath(xpath));
    }

}
