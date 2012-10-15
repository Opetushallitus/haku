package fi.vm.sade.oppija.haku.selenium;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.converter.FormModelToJsonString;
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

    protected final Selenium selenium;
    protected final WebDriver driver;

    public AbstractSeleniumTest() {
        super();
        WebDriver driver = new FirefoxDriver();
        this.driver = driver;
        this.selenium = new WebDriverBackedSelenium(driver, getBaseUrl());
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Override
    protected void login() {
        selenium.type("j_username", "admin");
        selenium.type("j_password", "admin");
        selenium.submit("login");
    }

    protected FormModelHelper initModel(FormModel formModel1) {
        driver.get(getBaseUrl() + "/admin/edit");
        login();
        final String convert = new FormModelToJsonString().convert(formModel1);

        selenium.type("model", convert);
        selenium.submit("tallenna");
        return new FormModelHelper(formModel1);
    }

    @After
    public void close() {
        driver.close();
    }

}
