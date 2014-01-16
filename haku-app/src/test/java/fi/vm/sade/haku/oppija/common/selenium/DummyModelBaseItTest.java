package fi.vm.sade.haku.oppija.common.selenium;

import com.google.common.base.Joiner;
import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.ui.selenium.DefaultValues;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGeneratorMock;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.KoodistoServiceMockImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;

public abstract class DummyModelBaseItTest extends AbstractSeleniumBase {

    protected WebDriver driver;
    protected Selenium selenium;
    protected DefaultValues defaultValues;
    public ApplicationSystemHelper applicationSystemHelper;

    @Before
    public void setUDummyModelBaseIt() throws Exception {
        defaultValues = new DefaultValues();
        FormGeneratorMock formGeneratorMock = new FormGeneratorMock(new KoodistoServiceMockImpl(), ASID);
        applicationSystemHelper = updateApplicationSystem(formGeneratorMock.createApplicationSystem());
        driver = seleniumContainer.getDriver();
        selenium = seleniumContainer.getSelenium();
    }

    protected void nextPhase() {
        driver.findElement(new By.ByClassName("right")).click();
    }

    protected void setValue(final String id, final String value) {
        WebElement element = driver.findElement(new By.ByName(id));
        String tagName = element.getTagName();
        if ("select".equals(tagName)) {
            new Select(element).selectByValue(value);
        } else if ("input".equals(tagName)) {
            String type = element.getAttribute("type");
            if ("radio".equals(type) || "checkbox".equals(type)) {
                clickByNameAndValue(id, value);
            } else {
                selenium.typeKeys(id, value);
            }
        }

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
        List<WebElement> elements = driver.findElements(new By.ByXPath("//select[option[@value='']]"));
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
                fail("name " + name + " not found");
            }
        }
    }

    protected void elementsPresentBy(By... byes) {
        for (By by : byes) {
            driver.findElement(by);
        }
    }

    protected void elementsNotPresentBy(By... byes) {
        for (By by : byes) {
            if (!driver.findElements(by).isEmpty()) {
                fail("element " + by.toString() + " not found");
            }
        }
    }

    protected void findById(final String... ids) {
        for (String id : ids) {
            driver.findElement(new By.ById(id));
        }
    }

    protected List<WebElement> getById(final String id) {
        return driver.findElements(new By.ById(id));
    }

    protected final void fillOut(final Map<String, String> values) {
        for (Map.Entry<String, String> questionAndAnswer : values.entrySet()) {
            System.out.println(questionAndAnswer.getKey() + "| "+ questionAndAnswer.getValue());
            setValue(questionAndAnswer.getKey(), questionAndAnswer.getValue());
        }
    }
}
