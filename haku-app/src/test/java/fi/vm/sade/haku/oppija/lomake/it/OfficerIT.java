package fi.vm.sade.haku.oppija.lomake.it;

import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.haku.oppija.common.selenium.LoginPage;
import fi.vm.sade.haku.oppija.hakemus.domain.Application;
import fi.vm.sade.haku.oppija.lomake.HakuClient;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.haku.oppija.ui.selenium.DefaultValues.KYSYMYS_POHJAKOULUTUS;
import static fi.vm.sade.haku.oppija.ui.selenium.DefaultValues.TUTKINTO_YLIOPPILAS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OfficerIT extends DummyModelBaseItTest {

    Logger log = LoggerFactory.getLogger(OfficerIT.class);

    @Value("${application.oid.prefix}")
    private String applicationOidPrefix;

    @Before
    public void beforeOfficerIt() throws Exception {
        String baseUrl = getBaseUrl();
        HakuClient hakuClient = new HakuClient(baseUrl + "lomake/", "application.json");
        hakuClient.apply();
        final LoginPage loginPage = new LoginPage(seleniumContainer.getSelenium());
        navigateToPath("user", "login");
        loginPage.login("officer");
        activate(applicationOidPrefix + ".00000000013");
        navigateToPath("virkailija", "hakemus");
    }


    @Test
    public void testSearchAndModify() throws Exception {
        clearSearch();
        clickSearch();
        WebElement applicationLink = findByClassName("application-link").get(0);
        applicationLink.click();
        checkApplicationState("Aktiivinen");
        List<WebElement> editLinks = findByClassName("edit-link");
        WebElement editLink = editLinks.get(1);
        editLink.click();
        clickByNameAndValue(KYSYMYS_POHJAKOULUTUS, TUTKINTO_YLIOPPILAS);
        setValue(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI, "3012");
        setValue(OppijaConstants.LUKIO_KIELI, "FI");
        seleniumContainer.getDriver().findElement(new By.ByClassName("save")).click();
        checkApplicationState("Puutteellinen");
    }

    private List<WebElement> SearchByTerm(final String term) {
        enterSearchTerm(term);
        clickSearch();
        seleniumContainer.getSelenium().waitForCondition("selenium.browserbot.getCurrentWindow().document.getElementsByClassName('application-link')", "1000");
        return findByClassName("application-link");
    }

    private List<WebElement> searchByTermAndState(final String term, Application.State state) {
        enterSearchTerm(term);
        selectState(state);
        clickSearch();
        seleniumContainer.getSelenium().waitForCondition("selenium.browserbot.getCurrentWindow().document.getElementsByClassName('application-link')", "1000");
        return findByClassName("application-link");
    }

    private void selectState(Application.State state) {
        Select stateSelect = new Select(seleniumContainer.getDriver().findElement(By.id("application-state")));
        stateSelect.selectByValue(state == null ? "" : state.toString());
    }

    private WebElement checkApplicationState(String applicationState) {

        return seleniumContainer.getDriver().findElement(By.xpath("//*[contains(.,'" + applicationState + "')]"));
    }

    private void enterSearchTerm(final String term) {
        setValue("entry", term);
    }

    private void clearSearch() {
        findByIdAndClick("reset-search");
        WebElement element = findElementById("hakukausiVuosi");
        element.clear();
        setValue("hakukausi", "");
        setValue("application-state", "");
    }

    private void clickSearch() {
        click("search-applications");
    }

    private void activate(String oid) {
        activate(oid, null);
    }

    private void activate(String oid, String screenshot) {
        clearSearch();
        if (screenshot != null) {
            screenshot(screenshot);
        }
        List<WebElement> webElements = SearchByTerm(oid);
        for (WebElement webElement : webElements) {
            if (webElement.getText().equals(oid)) {
                webElement.click();
                break;
            }
        }
        click("postProcessApplication");
        click("submit-dialog");

        navigateToPath("virkailija", "hakemus", oid, "activate");
        List<WebElement> passivateApplication = getById("passivateApplication");
        if (passivateApplication.isEmpty()) {
            List<WebElement> activateApplication = getById("activateApplication");
            if (!activateApplication.isEmpty()) {
                activateApplication.get(0).click();
                click("confirm-activation");
            }
        }
        click("back");
    }

    private void passivate() {
        findByIdAndClick("passivateApplication");
        setValue("passivation-reason", "reason");
        findByIdAndClick("submit_confirm");
    }

    private List<WebElement> searchByPreference(final String preference) {
        clearSearch();
        setValue("application-preference", preference);
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

    protected void elementsPresent(String... locations) {
        for (String location : locations) {
            assertTrue("Could not find element " + location, seleniumContainer.getSelenium().isElementPresent(location));
        }
    }
}
