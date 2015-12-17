package fi.vm.sade.haku.oppija.lomake.it;

import static fi.vm.sade.haku.oppija.ui.selenium.DefaultValues.KYSYMYS_POHJAKOULUTUS;
import static fi.vm.sade.haku.oppija.ui.selenium.DefaultValues.TUTKINTO_YLIOPPILAS;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.mongodb.DBObject;

import fi.vm.sade.haku.oppija.common.selenium.DummyModelBaseItTest;
import fi.vm.sade.haku.oppija.common.selenium.LoginPage;
import fi.vm.sade.haku.oppija.lomake.HakuClient;
import fi.vm.sade.haku.util.JsonTestData;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;

public class OfficerIT extends DummyModelBaseItTest {

    Logger log = LoggerFactory.getLogger(OfficerIT.class);

    @Value("${application.oid.prefix}")
    private String applicationOidPrefix;

    @Before
    public void beforeOfficerIt() throws Exception {
        mongoTemplate.getCollection("application").insert((List<DBObject>) JsonTestData.readTestData("kk-yhteishaku-hakemuksia.json"));
    }

    @Test
    public void testSearchAsEiKKVirkalija() throws Exception {
        final LoginPage loginPage = new LoginPage(seleniumContainer.getDriver());
        navigateToPath("user", "login");
        loginPage.login("eikkvirkailija");
        navigateToPath("virkailija", "hakemus");
        clearSearch();
        clickSearch();
        List<WebElement> applicationLinks = findByClassName("application-link");
        assertEquals(applicationLinks.size(), 1);
    }

    @Test
    public void testSearchAsKKVirkailija() throws Exception {
        final LoginPage loginPage = new LoginPage(seleniumContainer.getDriver());
        navigateToPath("user", "login");
        loginPage.login("kkvirkailija");
        navigateToPath("virkailija", "hakemus");
        clearSearch();
        Select haku = new Select(getById("application-system").get(0));
        haku.selectByVisibleText("Korkkaritesti 2015");
        clickSearch();
        List<WebElement> applicationLinks = findByClassName("application-link");
        assertEquals(applicationLinks.size(), 3);
    }

    @Test
    public void testSearchAndModify() throws Exception {
        String baseUrl = getBaseUrl();
        HakuClient hakuClient = new HakuClient(baseUrl + "lomake/", "application.json");
        hakuClient.apply();
        final LoginPage loginPage = new LoginPage(seleniumContainer.getDriver());
        navigateToPath("user", "login");
        loginPage.login("officer");
        activate(applicationOidPrefix + ".00000000013", "OfficerIt");
        navigateToPath("virkailija", "hakemus");
        clearSearch();
        setValue("entry", applicationOidPrefix + ".00000000013");
        clickSearch();
        WebElement applicationLink = findByClassName("application-link").get(0);
        applicationLink.click();
        List<WebElement> editLinks = findByClassName("edit-link");
        WebElement editLink = editLinks.get(1);
        editLink.click();
        waitForElement(120, By.id(KYSYMYS_POHJAKOULUTUS + "_" + TUTKINTO_YLIOPPILAS));
        findByAndAjaxClick(By.id(KYSYMYS_POHJAKOULUTUS + "_" + TUTKINTO_YLIOPPILAS));
        setValue(OppijaConstants.LUKIO_PAATTOTODISTUS_VUOSI, "3012");
        setValue(OppijaConstants.LUKIO_KIELI, "FI");
        seleniumContainer.getDriver().findElement(new By.ByClassName("save")).click();
        checkApplicationState("Puutteellinen");
    }

    private List<WebElement> SearchByTerm(final String term) {
        enterSearchTerm(term);
        clickSearch();
        new WebDriverWait(seleniumContainer.getDriver(), 1000)
                .until(new ExpectedCondition<Object>() {
                    @Override
                    public Object apply(WebDriver webDriver) {
                        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
                        return executor.executeScript("return document.getElementsByClassName('application-link')");
                    }
                });
        return findByClassName("application-link");
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
        findByIdAndClick("search-applications");
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
        findByIdAndClick("postProcessApplication", "submit-dialog");

        navigateToPath("virkailija", "hakemus", oid, "");
        List<WebElement> passivateApplication = getById("passivateApplication");
        if (passivateApplication.isEmpty()) {
            List<WebElement> activateApplication = getById("activateApplication");
            if (!activateApplication.isEmpty()) {
                activateApplication.get(0).click();
                findByIdAndClick("confirm-activation");
            }
        }
        findByIdAndClick("back");
    }
}
