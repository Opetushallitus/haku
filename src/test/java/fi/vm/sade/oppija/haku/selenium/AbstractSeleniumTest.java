package fi.vm.sade.oppija.haku.selenium;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.it.TomcatContainerTest;
import org.junit.After;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

/**
 * @author jukka
 * @version 10/15/121:13 PM}
 * @since 1.1
 */
public class AbstractSeleniumTest extends TomcatContainerTest {

    protected SeleniumHelper seleniumHelper;

    public AbstractSeleniumTest() {
        super();
        WebDriver driver = new FirefoxDriver();
        Selenium selenium = new WebDriverBackedSelenium(driver, getBaseUrl());
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        this.seleniumHelper = new SeleniumHelper(selenium, driver);
    }


    protected FormModelHelper initModel(FormModel formModel1) {

        final AdminEditPage adminEditPage = new AdminEditPage(getBaseUrl(), seleniumHelper.getSelenium());
        seleniumHelper.navigate(adminEditPage);
        adminEditPage.login("admin");
        adminEditPage.submitForm(formModel1);
        return new FormModelHelper(formModel1);
    }

    @After
    public void close() {
        seleniumHelper.close();
    }


}
