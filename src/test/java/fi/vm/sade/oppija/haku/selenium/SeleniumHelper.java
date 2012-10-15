package fi.vm.sade.oppija.haku.selenium;

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

    public SeleniumHelper(Selenium selenium, WebDriver driver) {
        this.selenium = selenium;
        this.driver = driver;
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
}
