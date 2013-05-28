package fi.vm.sade.oppija.lomake.it;

import fi.vm.sade.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.oppija.common.selenium.LoginPage;
import fi.vm.sade.oppija.lomake.HakuClient;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OfficerIT extends DummyModelBaseItTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        HakuClient hakuClient = new HakuClient(getBaseUrl() + "lomake/", "application.json");
        hakuClient.apply();
        final LoginPage loginPage = new LoginPage(seleniumHelper.getSelenium());
        navigateToPath("user", "login");
        loginPage.login("officer");
    }

    @Test
    public void testSearchAndModify() throws Exception {
        clickSearch();
        WebElement applicationLink = findByClassName("application-link").get(0);
        applicationLink.click();
        checkApplicationState("Aktiivinen");
        List<WebElement> editLinks = findByClassName("edit-link");
        WebElement editLink = editLinks.get(1);
        editLink.click();
        findByIdAndClick("POHJAKOULUTUS_tutkinto9");
        driver.findElement(new By.ByClassName("save")).click();
        checkApplicationState("Puutteellinen");
    }

    @Test
    public void testOrganization() throws Exception {
        driver.findElement(new By.ByClassName("label")).click();
        selenium.typeKeys("searchString", "Espoo");
        driver.findElement(new By.ById("search-organizations")).click();
        driver.findElement(new By.ById("1.2.246.562.10.10108401950"));
        findByIdAndClick("search-organizations");
        findById("1.2.246.562.10.10108401950");
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

    @Test
    public void testSearchByDod() throws Exception {
        assertTrue("Application not found", SearchByTerm("120100").isEmpty());
    }

    @Test
    public void testSearchByDodDots() throws Exception {
        assertTrue("Application not found", SearchByTerm("12.01.2000").isEmpty());
    }

    @Test
    public void testSearchByOid() throws Exception {
        assertTrue("Application not found", SearchByTerm(" 1.2.246.562.10.10108401950").isEmpty());
    }

    private List<WebElement> SearchByTerm(final String term) {
        enterSearchTerm(term);
        clickSearch();
        return findByClassName("application-link");
    }

    private void enterSearchTerm(final String term) {
        setValue("entry", term);
    }

    private void checkApplicationState(String applicationState) {
        driver.findElement(By.xpath("//*[contains(.,'" + applicationState + "')]"));
    }


    private void clickSearch() {
        findByIdAndClick("search-applications");
    }
}
