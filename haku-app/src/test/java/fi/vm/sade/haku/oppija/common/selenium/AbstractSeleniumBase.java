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

import com.google.common.base.Predicate;
import com.mongodb.BasicDBObject;
import fi.vm.sade.haku.oppija.common.it.TomcatContainerBase;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.hakutest.SeleniumContainer;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationOidDAOMongoImpl.SEQUENCE_FIELD;
import static fi.vm.sade.haku.oppija.hakemus.it.dao.impl.ApplicationOidDAOMongoImpl.SEQUENCE_NAME;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.fail;

public abstract class AbstractSeleniumBase extends TomcatContainerBase {
    @Autowired
    protected SeleniumContainer seleniumContainer;

    @Autowired
    protected MongoTemplate mongoTemplate;

    protected Logger logger;

    @Before
    public void before() {
        logger = LoggerFactory.getLogger(this.getClass());
        mongoTemplate.getDb().dropDatabase();
        mongoTemplate.getCollection(SEQUENCE_NAME)
                .insert(new BasicDBObject(SEQUENCE_FIELD, Long.valueOf(0)));
        seleniumContainer.logout();
        //seleniumContainer.getDriver().manage().deleteAllCookies();
    }

    protected void navigateTo(String url) {
        seleniumContainer.getDriver().get(url);
    }

    protected void waitForElement(final long seconds, final By by) {
        new WebDriverWait(seleniumContainer.getDriver(), seconds)
                .until(ExpectedConditions.presenceOfElementLocated(by));
        waitForAjax();
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
        clickByXPath("//*[@name='" + name + "' and @value='" + value + "']");
    }

    protected WebElement findBy(final By by) {
        WebDriver driver = seleniumContainer.getDriver();
        return driver.findElement(by);
    }

    protected void findByIdAndClick(final String... ids) {
        for (String id : ids) {
            click(By.id(id));
        }
    }

    protected void click(By by) {
        new WebDriverWait(seleniumContainer.getDriver(), 10, 100).until(ExpectedConditions.elementToBeClickable(by));
        int attempt = 1;
        StaleElementReferenceException lastException = null;
        while(attempt <= 3) {
            try {
                seleniumContainer.getDriver().findElement(by).click();
                waitForAjax();
                return;
            } catch(StaleElementReferenceException e) {
                lastException = e;
                logger.warn("Could not click " + by + ". Attempt:" + attempt, e);
            }
            attempt++;
        }
        throw lastException;
    }

    protected void clickByXPath(final String xpath) {
        click(By.xpath(xpath));
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
        waitForAjax();
    }

    protected void typeWithoutTab(final String id, final String text) {
        type(id, text, false);
        waitForMillis(500L);
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
        click(By.linkText(text));
    }

    protected void elementsPresentByName(final String... names) {
        for (String name : names) {
            seleniumContainer.getDriver().findElement(By.name(name));
        }
    }

    protected void elementsNotPresentByName(final String... names) {
        for (String name : names) {
            elementsNotPresentBy(By.name(name));
        }
    }

    protected void elementsNotPresentBy(final By by) {
        seleniumContainer.getDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            List<WebElement> elements = seleniumContainer.getDriver().findElements(by);
            if (!elements.isEmpty()) {
                fail("elements " + by.toString() + " found:" + elements.size());
            }
        }
        finally {
            seleniumContainer.getDriver().manage().timeouts().implicitlyWait(SeleniumContainer.IMPLICIT_WAIT_TIME_IN_SECONDS, TimeUnit.SECONDS);
        }
    }

    protected void elementsNotPresentById(String... locations) {
        for (String location : locations) {
            elementsNotPresentBy(By.id(location));
        }
    }

    protected void elementsNotPresentByXPath(String... locations) {
        for (String location : locations) {
            elementsNotPresentBy(By.xpath(location));
        }
    }

    protected void findById(final String... ids) {
        for (String id : ids) {
            seleniumContainer.getDriver().findElement(new By.ById(id));
        }
    }

    protected void waitForMillis(final long millis) {
        final long t1 = new Date().getTime();
        new WebDriverWait(seleniumContainer.getDriver(), (millis / 1000) + 1, 10).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return (new Date().getTime() - t1) > millis;
            }
        });
    }

    public void waitForAjax() {
        // explicit sleep is needed because bacon is grouping the request in 100ms intervals!
        waitForMillis(110);

        (new WebDriverWait(seleniumContainer.getDriver(), 5)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                JavascriptExecutor js = (JavascriptExecutor) d;
                return (Boolean) js.executeScript("return jQuery.active == 0");
            }
        });
    }

    protected List<WebElement> getById(final String id) {
        return seleniumContainer.getDriver().findElements(new By.ById(id));
    }
}
