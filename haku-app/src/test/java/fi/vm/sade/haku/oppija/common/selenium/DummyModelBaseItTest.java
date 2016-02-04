package fi.vm.sade.haku.oppija.common.selenium;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.ui.selenium.DefaultValues;
import fi.vm.sade.hakutest.SeleniumContainer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public abstract class DummyModelBaseItTest extends AbstractSeleniumBase {

    protected DefaultValues defaultValues = new DefaultValues();
    public ApplicationSystemHelper applicationSystemHelper;

    @Before
    public void setUDummyModelBaseIt() throws Exception {
        //FormGenerator formGeneratorMock = new FormGeneratorImpl(new KoodistoServiceMockImpl(), new HakuServiceMockImpl());
        //applicationSystemHelper = updateApplicationSystem(formGeneratorMock.generate(ASID));
        seleniumContainer.getDriver().get((getBaseUrl() + "lomakkeenhallinta/ALL"));
        waitForElement(60, By.id("published"));
    }


    protected void back() {
        seleniumContainer.getDriver().navigate().back();
    }

    protected void nextPhase(String expectedPhase) {
        click(new By.ByClassName("right"));
        expectPhase(expectedPhase);
    }

    protected void prevPhase(String expectedPhase) {
        click(new By.ByClassName("left"));
        expectPhase(expectedPhase);
    }

    protected void expectPhase(String expected) {
        new WebDriverWait(seleniumContainer.getDriver(), 30, 300L)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("nav-" + expected)));
        WebElement form = findBy(By.id("nav-" + expected));
        assertEquals("current", form.getAttribute("class"));
    }

    private void clickAllElements(List<WebElement> elements) {
        for (WebElement webElement : elements) {
            webElement.click();
        }
    }

    protected void setValue(final String id, final String value, final boolean wait) {
        if (wait) {
            waitForElement(20, By.name(id));
        }
        setValue(id, value);
    }

    protected void setValue(final String id, final String value) {
        WebDriver webDriver = seleniumContainer.getDriver();
        WebElement element = webDriver.findElement(new By.ByName(id));
        String tagName = element.getTagName();
        if ("select".equals(tagName)) {
            new Select(element).selectByValue(value);
        } else if ("input".equals(tagName)) {
            String type = element.getAttribute("type");
            if ("radio".equals(type)) {
                clickByNameAndValue(id, value);
            } else if ("checkbox".equals(type)) {
                findByIdAndClick(id);
            } else {
                type(id, value, true);
            }
        } else {
            type(id, value, true);
        }
        seleniumContainer.waitForAjax();
    }

    protected void navigateToFirstPhase() {
        String url = getBaseUrl() + "lomake/";
        seleniumContainer.getDriver().get(url);
        clickByHref(ASID);
    }

    protected void navigateToPath(final String... pathSegments) {
        Joiner joiner = Joiner.on("/").skipNulls();
        String[] baseUrl = new String[]{StringUtils.removeEnd(getBaseUrl(), "/")};
        String[] parts = ArrayUtils.addAll(baseUrl, pathSegments);
        String join = joiner.join(parts);
        seleniumContainer.getDriver().get(join);
    }

    protected void select() {
        List<WebElement> elements = seleniumContainer.getDriver().findElements(new By.ByXPath("//select[option[@value='']]"));
        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                Select select = new Select(element);
                if (select.getOptions().size() > 6) {
                    select.selectByIndex(6);
                }
            }
        }
    }

    protected void clickAllElementsByXPath(final String xpath) {
        List<WebElement> elements = seleniumContainer.getDriver().findElements(new By.ByXPath(xpath));
        clickAllElements(elements);
    }

    protected final void fillOut(final Map<String, String> values) {
        for (Map.Entry<String, String> questionAndAnswer : values.entrySet()) {
            setValue(questionAndAnswer.getKey(), questionAndAnswer.getValue(), true);
        }
    }

    protected void verifyDropdownSelection(String dropdownId, String expectedText, String expectedValue) {
        Select countryDropdown = new Select(seleniumContainer.getDriver().findElementById(dropdownId));
        WebElement selected = countryDropdown.getFirstSelectedOption();
        assertEquals(expectedText, selected.getText().trim());
        assertEquals(expectedValue, selected.getAttribute("value"));
    }
}
