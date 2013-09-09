package fi.vm.sade.oppija.lomake.it;

import fi.vm.sade.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.oppija.common.selenium.LoginPage;
import fi.vm.sade.oppija.hakemus.domain.Application;
import fi.vm.sade.oppija.lomake.HakuClient;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OfficerIT extends DummyModelBaseItTest {


    @Before
    public void setUp() throws Exception {
        String baseUrl = getBaseUrl();
        HakuClient hakuClient = new HakuClient(baseUrl + "lomake/", "application.json");
        hakuClient.apply();
        final LoginPage loginPage = new LoginPage(seleniumHelper.getSelenium());
        navigateToPath("user", "login");
        loginPage.login("officer");
        activate("1.2.3.4.5.00000000000");
        navigateToPath("virkailija", "hakemus");
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
    public void testSearchByPreference() {
        List<WebElement> applicationLinks = findByClassName("application-link");
        assertTrue("Applications found", applicationLinks.isEmpty());

        selenium.typeKeys("application-preference", "vosala"); // Kaivosalan
        selectState(null);
        clickSearch();
        applicationLinks = findByClassName("application-link");
        assertFalse("Applications not found", applicationLinks.isEmpty());

        clearSearch();
        selenium.typeKeys("application-preference", "123");
        selectState(null);
        clickSearch();
        applicationLinks = findByClassName("application-link");
        assertFalse("Applications not found", applicationLinks.isEmpty());

        clearSearch();
        selenium.typeKeys("application-preference", "notfound");
        selectState(null);
        clickSearch();
        applicationLinks = findByClassName("application-link");
        assertTrue("Applications found", applicationLinks.isEmpty());

    }

    @Test
    public void testEditControls() throws InterruptedException {
        clickSearch();
        WebElement applicationLink = findByClassName("application-link").get(0);
        applicationLink.click();
        List<WebElement> editLinks = findByClassName("edit-link");
        assertFalse("Edit links not found", editLinks.isEmpty());
        editLinks = findByClassName("edit-link");
        assertFalse("Edit links not found", editLinks.isEmpty());
        passivate();
        editLinks = findByClassName("edit-link");
        assertTrue("Edit links found", editLinks.isEmpty());
    }

    @Test
    public void testComments() {
        clickSearch();
        WebElement applicationLink = findByClassName("application-link").get(0);
        applicationLink.click();
        List<WebElement> editLinks = findByClassName("edit-link");
        WebElement editLink = editLinks.get(5);
        editLink.click();
        findByIdAndClick("lupaMarkkinointi");
        driver.findElement(new By.ByClassName("save")).click();
        selenium.typeKeys("note-text", "Uusi kommentti");
        findByIdAndClick("note-create");
        passivate();

        boolean received = false;
        boolean lisatiedot = false;
        boolean added = false;
        boolean passive = false;
        for (WebElement element : findByClassName("note-content")) {
            received = received || element.getText().contains("Hakemus vastaanotettu");
            lisatiedot = lisatiedot || element.getText().contains("Päivitetty vaihetta 'lisatiedot'");
            added = added || element.getText().contains("Uusi kommentti");
            passive = passive || element.getText().contains("Hakemus passivoitu: reason");
        }
        assertTrue(received);
        assertTrue(lisatiedot);
        assertTrue(added);
        assertTrue(passive);
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
        assertFalse("Application not found", SearchByTermAndState("topi", null).isEmpty());
        clearSearch();
        assertFalse("Application not found", SearchByTermAndState("topi", null).isEmpty());
        clearSearch();
        assertTrue("Application found", SearchByTermAndState("topi", Application.State.PASSIVE).isEmpty());
        clearSearch();
        assertFalse("Application found", SearchByTermAndState("topi", Application.State.ACTIVE).isEmpty());
    }

    @Test
    public void testSearchByNameNotFound() throws Exception {
        assertTrue("Application found", SearchByTermAndState("Notfound", null).isEmpty());
        clearSearch();
        assertTrue("Application found", SearchByTermAndState("Notfound", Application.State.ACTIVE).isEmpty());
        clearSearch();
        assertTrue("Application found", SearchByTermAndState("Notfound", Application.State.PASSIVE).isEmpty());
    }

    @Test
    public void testSearchByLastname() throws Exception {
        assertFalse("Application not found", SearchByTermAndState("Korhonen", null).isEmpty());
        clearSearch();
        assertFalse("Application not found", SearchByTermAndState("Korhonen", Application.State.ACTIVE).isEmpty());
        clearSearch();
        assertTrue("Application not found", SearchByTermAndState("Korhonen", Application.State.PASSIVE).isEmpty());
    }

    @Test
    public void testSearchBySsn() throws Exception {
        assertFalse("Application not found", SearchByTermAndState("270802-184A", null).isEmpty());
        clearSearch();
        assertFalse("Application not found", SearchByTermAndState("270802-184A", Application.State.ACTIVE).isEmpty());
        clearSearch();
        assertTrue("Application not found", SearchByTermAndState("270802-184A", Application.State.PASSIVE).isEmpty());
    }

    @Test
    public void testSearchByDob() throws Exception {
        assertTrue("Application not", SearchByTerm("120100").isEmpty());
        clearSearch();
        assertTrue("Application not found", SearchByTermAndState("120100", Application.State.PASSIVE).isEmpty());
    }

    @Test
    public void testSearchByDobDots() throws Exception {
        assertTrue("Application not", SearchByTerm("12.01.2000").isEmpty());
        clearSearch();
        assertTrue("Application not found", SearchByTermAndState("12.01.2000", Application.State.PASSIVE).isEmpty());
    }

    @Test
    public void testSearchByOid() throws Exception {
        assertTrue("Application not found", SearchByTerm(" 1.2.246.562.10.10108401950").isEmpty());
    }

    @Test
    public void testCreateNewApplicationAndSetPersonOid() {
        findByIdAndClick("create-application");
        Select asSelect = new Select(driver.findElement(By.id("asSelect")));
        asSelect.selectByIndex(0);
        findByIdAndClick("submit_confirm");
        driver.findElement(new By.ByLinkText("Lisää oppijanumero")).click();
        final String personOid = "1.3.4.5.6.434324324";
        WebElement element = driver.findElement(By.id("newPersonOid"));
        element.sendKeys(personOid);
        element.submit();
        assertTrue(selenium.isTextPresent(personOid));
    }

    @Test
    public void testPrintView() throws InterruptedException {


        clickSearch();
        WebElement applicationLink = findByClassName("application-link").get(0);
        applicationLink.click();
        WebElement printLink = findByClassName("print").get(0);

        final int windowsBefore = driver.getWindowHandles().size();
        printLink.click();
        ExpectedCondition<Boolean> windowCondition = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.getWindowHandles().size() == windowsBefore + 1;
            }
        };
        WebDriverWait waitForWindow = new WebDriverWait(driver, 5);
        waitForWindow.until(windowCondition);

        ArrayList<String> newTab = new ArrayList<String>(driver.getWindowHandles());

        driver.switchTo().window(newTab.get(1));
        assertTrue(driver.getCurrentUrl().contains("print"));
        assertTrue(selenium.isTextPresent("Korhonen"));
        driver.close();
        driver.switchTo().window(newTab.get(0));
    }

    private List<WebElement> SearchByTerm(final String term) {
        enterSearchTerm(term);
        clickSearch();
        selenium.waitForCondition("selenium.browserbot.getCurrentWindow().document.getElementsByClassName('application-link')", "1000");
        return findByClassName("application-link");
    }

    private List<WebElement> SearchByTermAndState(final String term, Application.State state) {
        enterSearchTerm(term);
        selectState(state);
        clickSearch();
        selenium.waitForCondition("selenium.browserbot.getCurrentWindow().document.getElementsByClassName('application-link')", "1000");
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

    private void activate(String oid) throws InterruptedException {
        navigateToPath("virkailija", "hakemus", oid, "addPersonAndAuthenticate");
    }

    private void passivate() {
        findByIdAndClick("passivateApplication");
        selenium.typeKeys("passivation-reason", "reason");
        findByIdAndClick("submit_confirm");
    }
}
