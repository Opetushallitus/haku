package fi.vm.sade.oppija.lomake.it;

import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.common.selenium.LoginPage;
import fi.vm.sade.oppija.lomake.HakuClient;
import fi.vm.sade.oppija.lomake.dao.impl.FormServiceMockImpl;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class OfficerIT extends AbstractSeleniumBase {

    @Before
    public void setUp() throws Exception {
        FormServiceMockImpl formModelDummyMemoryDao = new FormServiceMockImpl();
        updateIndexAndFormModel(formModelDummyMemoryDao.getModel());
        HakuClient hakuClient = new HakuClient(getBaseUrl() + "/lomake/", "application.json");
        hakuClient.apply();
        final LoginPage loginPage = new LoginPage(seleniumHelper.getSelenium());
        loginPage.login("officer");
    }

    @Test
    public void testList() throws Exception {
        WebDriver driver = seleniumHelper.getDriver();
        driver.findElement(new By.ById("search-applications")).click();
        List<WebElement> elements = driver.findElements(new By.ByClassName("application-link"));
        WebElement applicationLink = elements.get(0);
        applicationLink.click();
        driver.findElement(By.xpath("//*[contains(.,'ACTIVE')]"));
        List<WebElement> editLinks = driver.findElements(new By.ByClassName("edit-link"));
        WebElement editLink = editLinks.get(1);
        editLink.click();
        driver.findElement(new By.ById("millatutkinnolla_tutkinto6")).click();
        driver.findElement(new By.ByClassName("save")).click();
        driver.findElement(By.xpath("//*[contains(.,'INCOMPLETE')]"));
    }
}
