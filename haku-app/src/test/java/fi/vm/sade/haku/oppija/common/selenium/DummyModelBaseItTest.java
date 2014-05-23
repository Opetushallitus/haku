package fi.vm.sade.haku.oppija.common.selenium;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fi.vm.sade.haku.oppija.lomake.ApplicationSystemHelper;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationPeriod;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystem;
import fi.vm.sade.haku.oppija.lomake.domain.ApplicationSystemBuilder;
import fi.vm.sade.haku.oppija.lomake.domain.elements.Form;
import fi.vm.sade.haku.oppija.ui.selenium.DefaultValues;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.ElementUtil;
import fi.vm.sade.haku.virkailija.lomakkeenhallinta.util.OppijaConstants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public abstract class DummyModelBaseItTest extends AbstractSeleniumBase {

    protected DefaultValues defaultValues = new DefaultValues();
    public ApplicationSystemHelper applicationSystemHelper;

    @Before
    public void setUDummyModelBaseIt() throws Exception {
        //FormGenerator formGeneratorMock = new FormGeneratorImpl(new KoodistoServiceMockImpl(), new HakuServiceMockImpl());
        //applicationSystemHelper = updateApplicationSystem(formGeneratorMock.generate(ASID));
        seleniumContainer.getDriver().get((getBaseUrl() + "lomakkeenhallinta"));
    }


    protected void back() {
        seleniumContainer.getSelenium().goBack();
    }

    protected void nextPhase(String expectedPhase) {
        seleniumContainer.getDriver().findElement(new By.ByClassName("right")).click();
        expectPhase(expectedPhase);
    }

    protected void prevPhase(String expectedPhase) {
        seleniumContainer.getDriver().findElement(new By.ByClassName("left")).click();
        expectPhase(expectedPhase);
    }

    protected void expectPhase(String expected) {
        WebElement form = findBy(By.id("nav-" + expected));
        String clazz = form.getAttribute("class");
        assertEquals("current", clazz);
    }

    private void clickAllElements(List<WebElement> elements) {
        for (WebElement webElement : elements) {
            webElement.click();
        }
    }

    protected void setValue(final String id, final String value, final boolean wait) {
        if (wait) {
            new WebDriverWait(seleniumContainer.getDriver(), 10)
                    .until(ExpectedConditions.presenceOfElementLocated(By.name(id)));
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
                click(id);
            } else {
                type(id, value, true);
            }
        } else {
            type(id, value, true);
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

    protected void elementsNotPresent(String... locations) {
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

    public static ApplicationSystem createActiveApplicationSystem(final String id, Form form) {
        final Calendar instance = Calendar.getInstance();
        instance.roll(Calendar.YEAR, -1);
        Date start = new Date(instance.getTimeInMillis());
        instance.roll(Calendar.YEAR, 2);
        Date end = new Date(instance.getTimeInMillis());
        List<ApplicationPeriod> applicationPeriods = Lists.newArrayList(new ApplicationPeriod(start, end));
        return new ApplicationSystemBuilder().addId(id).addForm(form)
                .addName(ElementUtil.createI18NAsIs("test application period"))
                .addApplicationPeriods(applicationPeriods)
                .addHakukausiUri(OppijaConstants.HAKUKAUSI_SYKSY)
                .addApplicationSystemType(OppijaConstants.VARSINAINEN_HAKU)
                .get();
    }
}
