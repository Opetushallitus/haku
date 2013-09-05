package fi.vm.sade.oppija.common.selenium;

import com.google.common.base.Joiner;
import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.common.koodisto.impl.KoodistoServiceMockImpl;
import fi.vm.sade.oppija.lomakkeenhallinta.yhteishaku2013.FormGeneratorMock;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.Assert.fail;

public abstract class DummyModelBaseItTest extends AbstractSeleniumBase {

    protected WebDriver driver;
    protected Selenium selenium;

    @Before
    public void setUDummyModelBaseIt() throws Exception {
        FormGeneratorMock formGeneratorMock = new FormGeneratorMock(new KoodistoServiceMockImpl(), ASID);
        updateApplicationSystem(formGeneratorMock.createApplicationSystem());
        driver = seleniumHelper.getDriver();
        selenium = seleniumHelper.getSelenium();
    }

    protected void nextPhase() {
        driver.findElement(new By.ByClassName("right")).click();
    }

    protected void setValue(final String id, final String value) {
        WebElement element = driver.findElement(new By.ById(id));
        if ("select".equals(element.getTagName())) {
            Select followUpSelect = new Select(element);
            followUpSelect.selectByValue(value);
        } else if ("input".equals(element.getTagName())) {
            selenium.typeKeys(id, value);
        }

    }

    protected void navigateToPhase(final String phaseId) {
        Joiner joiner = Joiner.on("/").skipNulls();
        String url = joiner.join(StringUtils.removeEnd(getBaseUrl(), "/"), "lomake", ASID, phaseId);
        driver.get(url);
    }

    protected void navigateToFirstPhase() {
        String url = getBaseUrl() + "lomake/";
        driver.get(url);
        driver.findElement(new By.ById(ASID)).click();
    }

    protected void navigateToPath(final String... pathSegments) {
        Joiner joiner = Joiner.on("/").skipNulls();
        String[] baseUrl = new String[]{StringUtils.removeEnd(getBaseUrl(), "/")};
        String[] parts = ArrayUtils.addAll(baseUrl, pathSegments);
        String join = joiner.join(parts);
        driver.get(join);
    }

    protected void select() {
        List<WebElement> elements = driver.findElements(new By.ByXPath("//select[option[@selected and @disabled and @value='']]"));
        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                Select select = new Select(element);
                if (select.getOptions().size() > 6) {
                    select.selectByIndex(6);
                } else {
                    select.selectByIndex(2);
                }
            }
        }
    }

    protected void clickAllElementsByXPath(final String xpath) {
        List<WebElement> elements = driver.findElements(new By.ByXPath(xpath));
        for (WebElement element : elements) {
            element.click();
        }
    }

    protected void elementsPresentByName(final String... names) {
        for (String name : names) {
            driver.findElement(By.name(name));
        }
    }

    protected void elementsNotPresentByName(final String... names) {
        for (String name : names) {
            if (!driver.findElements(By.name(name)).isEmpty()) {
                fail("name " + name + " found");
            }
        }
    }
}
