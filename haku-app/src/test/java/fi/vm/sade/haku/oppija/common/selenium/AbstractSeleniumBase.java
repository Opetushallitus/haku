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
import static junit.framework.TestCase.assertFalse;

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
        seleniumContainer.getDriver().manage().deleteAllCookies();
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
        seleniumContainer.getDriver().findElementByXPath("//*[@name='" + name + "' and @value='" + value + "']").click();
    }

    protected WebElement findBy(final By by) {
        WebDriver driver = seleniumContainer.getDriver();
        return driver.findElement(by);
    }

    protected void findByIdAndClick(final String... ids) {
        for (String id : ids) {
            seleniumContainer.getDriver().findElement(By.id(id)).click();
        }
    }

    protected void findByAndAjaxClick(By by){
        seleniumContainer.getDriver().findElement(by).click();
        seleniumContainer.waitForAjax();
    }

    protected void clickByHref(final String... locations) {
        for (String location : locations) {
            WebDriver driver = seleniumContainer.getDriver();
            for (WebElement anchor : driver.findElements(By.tagName("a"))) {
                if (anchor.getAttribute("href").contains(location)) {
                    anchor.click();
                    break;
                }
            }
        }
    }

    protected void type(final String id, final String text, boolean includeTab) {
        seleniumContainer.getDriver().findElement(By.id(id)).sendKeys(text + ((includeTab) ? "\t" : ""));
    }

    protected void typeWithoutTab(final String id, final String text) {
        type(id, text, false);
    }

    protected void selectByValue(final String id, final String value) {
        Select select = new Select(findBy(new By.ById(id)));
        select.selectByValue(value);
    }

    protected WebElement findElementById(final String id) {
        return seleniumContainer.getDriver().findElement(By.id(id));
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

    protected void elementsPresent(String xpath) {
        assertFalse("Could not find element " + xpath,
                seleniumContainer.getDriver().findElements(By.xpath(xpath)).isEmpty());
    }

    protected boolean isTextPresent(final String text) {
        return seleniumContainer.getDriver().findElement(By.tagName("body")).getText().contains(text);
    }

    protected void clickLinkByText(final String text) {
        findByAndAjaxClick(By.linkText(text));
    }
}
