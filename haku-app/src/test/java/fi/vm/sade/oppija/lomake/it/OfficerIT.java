package fi.vm.sade.oppija.lomake.it;

import com.thoughtworks.selenium.Selenium;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OfficerIT extends AbstractSeleniumBase {

    private WebDriver driver;
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        FormServiceMockImpl formModelDummyMemoryDao = new FormServiceMockImpl();
        updateIndexAndFormModel(formModelDummyMemoryDao.getModel());
        HakuClient hakuClient = new HakuClient(getBaseUrl() + "lomake/", "application.json");
        hakuClient.apply();
        final LoginPage loginPage = new LoginPage(seleniumHelper.getSelenium());
        loginPage.login("officer");
        driver = seleniumHelper.getDriver();
        selenium = seleniumHelper.getSelenium();
    }

    @Test
    public void testSearchAndModify() throws Exception {
        clickSearch();
        WebElement applicationLink = findApplicationLinks().get(0);
        applicationLink.click();
        checkApplicationState("Voimassa");
        List<WebElement> editLinks = driver.findElements(new By.ByClassName("edit-link"));
        WebElement editLink = editLinks.get(1);
        editLink.click();
        driver.findElement(new By.ById("millatutkinnolla_tutkinto6")).click();
        driver.findElement(new By.ByClassName("save")).click();
        checkApplicationState("Puutteellinen");
    }

    @Test
    public void testOrganization() throws Exception {
        WebDriver driver = seleniumHelper.getDriver();
        Selenium selenium = seleniumHelper.getSelenium();
        driver.findElement(new By.ByClassName("label")).click();
        selenium.typeKeys("searchString", "Espoo");
        driver.findElement(new By.ById("search-organizations")).click();
        driver.findElement(new By.ById("1.2.246.562.10.10108401950"));
    }

    @Test
    public void testSearchByName() throws Exception {
        assertFalse("Application not found", SearchByTerm("topi").isEmpty());
    }

    @Test
    public void testSearchByNameNotFound() throws Exception {
        assertTrue("Application found", SearchByTerm("Notfound").isEmpty());
    }

    @Test
    public void testSearchByLastname() throws Exception {
        assertFalse("Application not found", SearchByTerm("Korhonen").isEmpty());
    }

    @Test
    public void testSearchBySsn() throws Exception {
        assertFalse("Application not found", SearchByTerm("270802-184A").isEmpty());
    }

    private List<WebElement> SearchByTerm(final String term) {
        enterSearchTerm(term);
        clickSearch();
        return findApplicationLinks();
    }

    private void enterSearchTerm(final String term) {
        selenium.typeKeys("entry", term);
    }

    private void checkApplicationState(String applicationState) {
        driver.findElement(By.xpath("//*[contains(.,'" + applicationState + "')]"));
    }

    private List<WebElement> findApplicationLinks() {
        return driver.findElements(new By.ByClassName("application-link"));
    }

    private void clickSearch() {
        driver.findElement(new By.ById("search-applications")).click();
    }
}
