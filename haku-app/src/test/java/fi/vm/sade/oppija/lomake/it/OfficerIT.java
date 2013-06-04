package fi.vm.sade.oppija.lomake.it;

import fi.vm.sade.oppija.common.it.OfficerClient;
import fi.vm.sade.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.oppija.common.selenium.LoginPage;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.HakuClient;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

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
        activate();
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
    public void testEditControlsPassive() throws InterruptedException {
        clickSearch();
        SearchByTermAndState("", null);
        WebElement applicationLink = findByClassName("application-link").get(0);
        applicationLink.click();
        List<WebElement> editLinks = findByClassName("edit-link");
        assertFalse("Edit links not found", editLinks.isEmpty());
        activate();
        assertFalse("Edit links not found", editLinks.isEmpty());
    }

    @Test
    public void testEditControlsActive() throws InterruptedException {
        activate();
        clickSearch();
        WebElement applicationLink = findByClassName("application-link").get(0);
        applicationLink.click();
        List<WebElement> editLinks = findByClassName("edit-link");
        assertFalse("Edit links not found", editLinks.isEmpty());
        activate();
        editLinks = findByClassName("edit-link");
        assertFalse("Edit links not found", editLinks.isEmpty());
        findByIdAndClick("passivateApplication");
        editLinks = findByClassName("edit-link");
        assertTrue("Edit links found", editLinks.isEmpty());
    }


    @Test
    public void testOrganization() throws Exception {
        activate();
        driver.findElement(new By.ByClassName("label")).click();
        selenium.typeKeys("searchString", "Espoo");
        driver.findElement(new By.ById("search-organizations")).click();
        driver.findElement(new By.ById("1.2.246.562.10.10108401950"));
        findByIdAndClick("search-organizations");
        findById("1.2.246.562.10.10108401950");
    }

    @Test
    public void testSearchByName() throws Exception {
        activate();
        assertFalse("Application not found", SearchByTerm("topi").isEmpty());
        clearSearch();
        assertFalse("Application not found", SearchByTermAndState("topi", null).isEmpty());
        clearSearch();
        assertTrue("Application found", SearchByTermAndState("topi", Application.State.PASSIVE).isEmpty());
    }

    @Test
    public void testSearchByNameNotFound() throws Exception {
        activate();
        assertTrue("Application found", SearchByTerm("Notfound").isEmpty());
        clearSearch();
        assertTrue("Application found", SearchByTermAndState("Notfound", null).isEmpty());
        clearSearch();
        assertTrue("Application found", SearchByTermAndState("Notfound", Application.State.PASSIVE).isEmpty());
    }

    @Test
    public void testSearchByLastname() throws Exception {
        activate();
        assertFalse("Application not found", SearchByTerm("Korhonen").isEmpty());
        clearSearch();
        assertFalse("Application not found", SearchByTermAndState("Korhonen", null).isEmpty());
        clearSearch();
        assertTrue("Application not found", SearchByTermAndState("Korhonen", Application.State.PASSIVE).isEmpty());
    }

    @Test
    public void testSearchBySsn() throws Exception {
        activate();
        assertFalse("Application not found", SearchByTerm("270802-184A").isEmpty());
        clearSearch();
        assertFalse("Application not found", SearchByTermAndState("270802-184A", null).isEmpty());
        clearSearch();
        assertTrue("Application not found", SearchByTermAndState("270802-184A", Application.State.PASSIVE).isEmpty());
    }

    @Test
    public void testSearchByDob() throws Exception {
        activate();
        assertTrue("Application not", SearchByTerm("120100").isEmpty());
        clearSearch();
        assertTrue("Application not found", SearchByTermAndState("120100", Application.State.PASSIVE).isEmpty());
    }

    @Test
    public void testSearchByDobDots() throws Exception {
        activate();
        assertTrue("Application not", SearchByTerm("12.01.2000").isEmpty());
        clearSearch();
        assertTrue("Application not found", SearchByTermAndState("12.01.2000", Application.State.PASSIVE).isEmpty());
    }

    @Test
    public void testSearchByOid() throws Exception {
        activate();
        assertTrue("Application not found", SearchByTerm(" 1.2.246.562.10.10108401950").isEmpty());
    }

    private List<WebElement> SearchByTerm(final String term) {
        enterSearchTerm(term);
        clickSearch();
        return findByClassName("application-link");
    }

    private List<WebElement> SearchByTermAndState(final String term, Application.State state) {
        enterSearchTerm(term);
        selectState(state);
        clickSearch();
        return findByClassName("application-link");
    }

    private void enterSearchTerm(final String term) {
        setValue("entry", term);
    }

    private void selectState(Application.State state) {
        Select stateSelect = new Select(driver.findElement(By.id("application-state")));
        stateSelect.selectByValue(state == null ? "" : state.toString());
    }

    private void checkApplicationState(String applicationState) {
        driver.findElement(By.xpath("//*[contains(.,'" + applicationState + "')]"));
    }

    private void clearSearch() {
        findByIdAndClick("reset-search");
    }

    private void clickSearch() {
        findByIdAndClick("search-applications");
    }

    private void activate() throws InterruptedException {
        OfficerClient officerClient = new OfficerClient(getBaseUrl() + "virkailija/");
        officerClient.addPersonAndAuthenticate("1.2.3.4.5.999");
        Thread.sleep(5000);
    }
}
