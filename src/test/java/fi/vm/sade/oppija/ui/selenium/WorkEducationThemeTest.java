package fi.vm.sade.oppija.ui.selenium;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.dao.impl.FormModelDummyMemoryDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.fail;

/**
 * @author Hannu Lyytikainen
 */
public class WorkEducationThemeTest extends AbstractSeleniumBase {

    private FormModelHelper formModelHelper;

    @Before
    public void init() {
        FormModelDummyMemoryDaoImpl formModel = new FormModelDummyMemoryDaoImpl();
        this.formModelHelper = initModel(formModel.getModel());
    }

    @Test
    public void testWorkExperienceShown() {
        final String startUrl = formModelHelper.getFormUrl(formModelHelper.getFirstForm().getPhase("hakutoiveet"));

        Selenium selenium = seleniumHelper.getSelenium();

        WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + startUrl);

        driver.findElement(By.id("preference1-Opetuspiste"));
        selenium.typeKeys("preference1-Opetuspiste", "Hel");
        driver.findElement(By.linkText("Helsingin sosiaali- ja terveysalan oppilaitos, Laakson koulutusyksikkö")).click();
        driver.findElement(By.xpath("//option[@data-id='873']")).click();

        driver.findElement(new By.ByClassName("right")).click();
        driver.findElement(new By.ByClassName("right")).click();

        driver.findElement(new By.ById("tyokokemuskuukaudet"));
    }

    @Test
    public void testWorkExperienceNotShown() {
        final String startUrl = formModelHelper.getFormUrl(formModelHelper.getFirstForm().getPhase("hakutoiveet"));

        Selenium selenium = seleniumHelper.getSelenium();

        WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + startUrl);

        driver.findElement(By.id("preference1-Opetuspiste"));
        selenium.typeKeys("preference1-Opetuspiste", "Hel");
        driver.findElement(By.linkText("Helsingin sosiaali- ja terveysalan oppilaitos, Laakson koulutusyksikkö")).click();
        driver.findElement(By.xpath("//option[@data-id='776']")).click();

        driver.findElement(new By.ByClassName("right")).click();
        driver.findElement(new By.ByClassName("right")).click();

        try {
            driver.findElement(new By.ById("tyokokemuskuukaudet"));
            fail();
        }
        catch (NoSuchElementException e) {
            // test passed
        }
    }
}
