package fi.vm.sade.oppija.haku;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.haku.selenium.SeleniumHelper;
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
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
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
