package fi.vm.sade.haku.oppija.common.selenium;

import com.google.common.base.Joiner;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.ui.selenium.DefaultValues;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.hakulomakepohja.FormGeneratorMock;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.koodisto.impl.KoodistoServiceMockImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public abstract class DummyModelBaseItTest extends AbstractSeleniumBase {

    protected DefaultValues defaultValues;
    public ApplicationSystemHelper applicationSystemHelper;

    @Before
    public void setUDummyModelBaseIt() throws Exception {
        defaultValues = new DefaultValues();
        FormGeneratorMock formGeneratorMock = new FormGeneratorMock(new KoodistoServiceMockImpl(), ASID);
        applicationSystemHelper = updateApplicationSystem(formGeneratorMock.createApplicationSystem());
    }


    protected void back() {
        seleniumContainer.getSelenium().goBack();
    }

    protected void nextPhase() {
        seleniumContainer.getDriver().findElement(new By.ByClassName("right")).click();
    }

    protected void prevPhase() {
        seleniumContainer.getDriver().findElement(new By.ByClassName("left")).click();
    }

    private void clickAllElements(List<WebElement> elements) {
        for (WebElement webElement : elements) {
            webElement.click();
        }
    }

    protected void setValue(final String id, final String value) {
        List<WebElement> elements = seleniumContainer.getDriver().findElements(new By.ByName(id));
        if (elements.size() > 1) {
            clickByNameAndValue(id, value);
        } else {
            WebElement element = elements.get(0);
            String tagName = element.getTagName();
            if ("select".equals(tagName)) {
                new Select(element).selectByValue(value);
            } else if ("input".equals(tagName)) {
                String type = element.getAttribute("type");
                if ("radio".equals(type)) {
                    clickByNameAndValue(id, value);
                } else if ("checkbox".equals(type)) {
                    element.click();
                } else {
                    seleniumContainer.getSelenium().typeKeys(id, value);
                }
            }
        }

    }

    protected void navigateToFirstPhase() {
        String url = getBaseUrl() + "lomake/";
        seleniumContainer.getDriver().get(url);
        click(ASID);
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

    protected void elementsPresentByName(final String... names) {
        for (String name : names) {
            seleniumContainer.getDriver().findElement(By.name(name));
        }
    }

    protected void elementsNotPresentByName(final String... names) {
        for (String name : names) {
            if (!seleniumContainer.getDriver().findElements(By.name(name)).isEmpty()) {
                fail("name " + name + " not found");
            }
        }
    }

    protected void elementsNotPresentBy(String... locations) {
        for (String location : locations) {
            assertFalse("Found element " + location, seleniumContainer.getSelenium().isElementPresent(location));
        }
    }

    protected void findById(final String... ids) {
        for (String id : ids) {
            seleniumContainer.getDriver().findElement(new By.ById(id));
        }
    }

    protected List<WebElement> getById(final String id) {
        return seleniumContainer.getDriver().findElements(new By.ById(id));
    }

    protected final void fillOut(final Map<String, String> values) {
        for (Map.Entry<String, String> questionAndAnswer : values.entrySet()) {
            setValue(questionAndAnswer.getKey(), questionAndAnswer.getValue());
        }
    }
}
