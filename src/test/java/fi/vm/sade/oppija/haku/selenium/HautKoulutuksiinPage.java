package fi.vm.sade.oppija.haku.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author jukka
 * @version 10/15/124:00 PM}
 * @since 1.1
 */
public class HautKoulutuksiinPage extends LoginPage implements PageObject {

    private final String baseUrl;
    private final SeleniumHelper seleniumHelper;

    public HautKoulutuksiinPage(String baseUrl, SeleniumHelper seleniumHelper) {
        super(seleniumHelper.getSelenium());
        this.baseUrl = baseUrl;
        this.seleniumHelper = seleniumHelper;
    }

    protected void login() {
        seleniumHelper.getDriver().get(baseUrl);
        final WebElement element = seleniumHelper.getDriver().findElement(By.linkText("Kirjaudu sisään"));
        element.click();
        login("test");
    }

    @Override
    public String getUrl() {
        return baseUrl + "/oma";
    }

}
