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

package fi.vm.sade.haku.oppija.common.selenium;

import com.mongodb.BasicDBObject;
import fi.vm.sade.haku.oppija.common.it.TomcatContainerBase;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.lomake.SeleniumContainer;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationOidDAOMongoImpl.SEQUENCE_FIELD;
import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationOidDAOMongoImpl.SEQUENCE_NAME;
import static org.junit.Assert.assertTrue;

public abstract class AbstractSeleniumBase extends TomcatContainerBase {


    @Autowired
    protected SeleniumContainer seleniumContainer;

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Before
    public void before() {
        mongoTemplate.getDb().dropDatabase();
        mongoTemplate.getCollection(SEQUENCE_NAME)
                .insert(new BasicDBObject(SEQUENCE_FIELD, Long.valueOf(0)));
        seleniumContainer.logout();
    }

    protected void navigateTo(String url) {
        seleniumContainer.getDriver().get(url);
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
        WebDriver driver = seleniumContainer.getDriver();
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File("target/" + filename + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void clickByNameAndValue(final String name, final String value) {
        seleniumContainer.getSelenium().click("//*[@name='" + name + "' and @value='" + value + "']");
    }

    protected WebElement findBy(final By by) {
        WebDriver driver = seleniumContainer.getDriver();
        return driver.findElement(by);
    }

    protected void findByIdAndClick(final String... ids) {
        for (String id : ids) {
            seleniumContainer.getSelenium().click("//*[@id = '" + id + "']");
        }
    }

    protected void click(final String... locations) {
        for (String location : locations) {
            seleniumContainer.getSelenium().click(location);
        }
    }

    protected String getTrimmedTextById(final String id) {
        return seleniumContainer.getSelenium().getText("//*[@id = '" + id + "']").trim();
    }

    protected void type(final String locator, final String text, boolean includeTab) {
        seleniumContainer.getSelenium().typeKeys(locator, text + ((includeTab) ? "\t" : ""));
    }

    protected void typeWithoutTab(final String locator, final String text) {
        type(locator, text, false);
    }

    protected void selectByValue(final String id, final String value) {
        Select select = new Select(findBy(new By.ById(id)));
        select.selectByValue(value);
    }

    protected WebElement findElementById(final String id) {
        return seleniumContainer.getDriver().findElement(By.id(id));
    }

    protected void findById(final String... ids) {
        for (String id : ids) {
            findBy(new By.ById(id));
        }
    }

    protected WebElement findByXPath(final String xpath) {
        return findBy(By.xpath(xpath));
    }


    protected List<WebElement> findByClassName(final String... classNames) {
        List<WebElement> elements = new ArrayList<WebElement>();
        for (String className : classNames) {
            elements.addAll(seleniumContainer.getDriver().findElements(new By.ByClassName(className)));
        }
        return elements;
    }

    protected void elementsPresent(String... locations) {
        for (String location : locations) {
            assertTrue("Could not find element " + location, seleniumContainer.getSelenium().isElementPresent(location));
        }
    }

    protected boolean isTextPresent(final String text) {
        return seleniumContainer.getSelenium().isTextPresent(text);
    }

    protected void clickLinkByText(final String text) {
        seleniumContainer.getDriver().findElement(By.linkText(text)).click();
    }
}
