package fi.vm.sade.haku.oppija.lomake.it;

import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.haku.oppija.common.selenium.LoginPage;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.HakuClient;
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

import static fi.vm.sade.haku.oppija.ui.selenium.DefaultValues.KYSYMYS_POHJAKOULUTUS;
import static fi.vm.sade.haku.oppija.ui.selenium.DefaultValues.TUTKINTO_YLIOPPILAS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OfficerIT extends DummyModelBaseItTest {

    @Before
    public void beforeOfficerIt() throws Exception {
        String baseUrl = getBaseUrl();
        HakuClient hakuClient = new HakuClient(baseUrl + "lomake/", "application.json");
        hakuClient.apply();
        final LoginPage loginPage = new LoginPage(seleniumContainer.getSelenium());
        navigateToPath("user", "login");
        loginPage.login("officer");
        activate("1.2.3.4.5.00000000000");
        navigateToPath("virkailija", "hakemus");
    }

    @Test
    public void testSearchAndModify() throws Exception {
        resetSemesterAndYear();
        clickSearch();
        WebElement applicationLink = findByClassName("application-link").get(0);
        applicationLink.click();
        checkApplicationState("Aktiivinen");
        List<WebElement> editLinks = findByClassName("edit-link");
        WebElement editLink = editLinks.get(1);
        editLink.click();
        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_YLIOPPILAS);
        driver.findElement(new By.ByClassName("save")).click();
        checkApplicationState("Puutteellinen");
    }


    @Test
    public void testEditControls() {
        resetSemesterAndYear();
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
        resetSemesterAndYear();
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
    public void testSearch() throws Exception {
        testSearchByTermAndState();
        testSearchByPreference();
    }


    private void testSearchByPreference() {
        assertFalse("Applications not found", searchByPreference("vosala").isEmpty());
        assertFalse("Applications not found", searchByPreference("123").isEmpty());
        assertTrue("Applications found", searchByPreference("notfound").isEmpty());

    }

    private void testSearchByTermAndState() throws Exception {
        shouldFindByTerm("topi");
        shouldNotFindByTermAndState("topi", Application.State.PASSIVE);
        shouldNotFindByTermAndState("topi", Application.State.INCOMPLETE);
        shouldNotFindByTermAndState("Notfound", null);
        shouldNotFindByTermAndState("Notfound", Application.State.ACTIVE);
        shouldNotFindByTermAndState("Notfound", Application.State.PASSIVE);
        shouldFindByTermAndState("Korhonen", null);
        shouldFindByTermAndState("Korhonen", Application.State.ACTIVE);
        shouldNotFindByTermAndState("Korhonen", Application.State.PASSIVE);
        shouldFindByTermAndState("270802-184A", null);
        shouldFindByTermAndState("270802-184A", Application.State.ACTIVE);
        shouldNotFindByTermAndState("270802-184A", Application.State.PASSIVE);
        shouldFindByTerm("27.08.1902");
        //shouldFindByTerm("270802");
        shouldNotFindByTerm("120100");
        shouldNotFindByTerm("12.01.2000");

        shouldNotFindByTerm("1.2.246.562.10.10108401950");
    }

    @Test
    public void testCreateNewApplicationAndSetPersonOid() {
        findByIdAndClick("create-application");
        Select asSelect = new Select(driver.findElement(By.id("asSelect")));
        asSelect.selectByIndex(0);
        findByIdAndClick("submit_confirm");
        String oid = getTrimmedTextById("_infocell_oid");
        findByIdAndClick("back");
        activate(oid);
        driver.findElement(new By.ByLinkText(oid)).click();
        driver.findElement(new By.ByLinkText("Lisää oppijanumero")).click();
        screenshot("personOid1");
        WebElement element = driver.findElement(By.id("addStudentOidForm"));
        element.submit();
        screenshot("personOid2");
        String personOid = getTrimmedTextById("_infocell_henkilonumero");
        String studentOid = getTrimmedTextById("_infocell_oppijanumero");
        assertTrue(studentOid.contains(personOid));
    }

    @Test
    public void View() {
        resetSemesterAndYear();
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

    private List<WebElement> searchByTermAndState(final String term, Application.State state) {
        enterSearchTerm(term);
        selectState(state);
        clickSearch();
        selenium.waitForCondition("selenium.browserbot.getCurrentWindow().document.getElementsByClassName('application-link')", "1000");
        return findByClassName("application-link");
    }

    private void selectState(Application.State state) {
        Select stateSelect = new Select(driver.findElement(By.id("application-state")));
        stateSelect.selectByValue(state == null ? "" : state.toString());
    }

    private WebElement checkApplicationState(String applicationState) {
        return driver.findElement(By.xpath("//*[contains(.,'" + applicationState + "')]"));
    }

    private void enterSearchTerm(final String term) {
        setValue("entry", term);
    }

    private void clearSearch() {
        findByIdAndClick("reset-search");
    }

    private void clickSearch() {
        findByIdAndClick("search-applications");
    }

    private void resetSemesterAndYear() {
        WebElement element = driver.findElement(new By.ById("hakukausiVuosi"));
        element.clear();
        setValue("hakukausi", "");
    }

    private void activate(String oid) {
        clearSearch();
        List<WebElement> webElements = SearchByTerm(oid);
        for (WebElement webElement : webElements) {
            if (webElement.getText().equals(oid)) {
                webElement.click();
                break;
            }
        }
        findByIdAndClick("postProcessApplication");
        findByIdAndClick("submit-dialog");

        navigateToPath("virkailija", "hakemus", oid, "activate");
        List<WebElement> passivateApplication = getById("passivateApplication");
        if (passivateApplication.isEmpty()) {
            List<WebElement> activateApplication = getById("activateApplication");
            if (!activateApplication.isEmpty()) {
                activateApplication.get(0).click();
                findByIdAndClick("conform-activation");


            }
        }
        findByIdAndClick("back");
    }

    private void passivate() {
        findByIdAndClick("passivateApplication");
        selenium.typeKeys("passivation-reason", "reason");
        findByIdAndClick("submit_confirm");
    }

    private List<WebElement> searchByPreference(final String preference) {
        clearSearch();
        selenium.typeKeys("application-preference", preference);
        clickSearch();
        return findByClassName("application-link");
    }

    private void shouldFindByTermAndState(final String term, final Application.State state) {
        clearSearch();
        assertFalse("Application not found", searchByTermAndState(term, state).isEmpty());
    }

    private void shouldNotFindByTermAndState(final String term, final Application.State state) {
        clearSearch();
        assertTrue("Application found", searchByTermAndState(term, state).isEmpty());
    }

    private void shouldFindByTerm(final String term) {
        clearSearch();
        assertFalse("Application found", SearchByTerm(term).isEmpty());
    }

    private void shouldNotFindByTerm(final String term) {
        clearSearch();
        assertTrue("Application not", SearchByTerm(term).isEmpty());
    }
}
