package fi.vm.sade.oppija.haku.selenium;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.it.AbstractRemoteTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertNotNull;

/**
 * @author Hannu Lyytikainen
 */
public class GradeGridIT extends AbstractSeleniumBase {

    private FormModelHelper formModelHelper;

    @Before
    public void init() {
        FormModelDummyMemoryDaoImpl dummyMem = new FormModelDummyMemoryDaoImpl();
        initModel(dummyMem.getModel());
    }

    @Test
    public void testTableExists() {
        final String url = getBaseUrl() + "/" + "lomake/Yhteishaku/yhteishaku/arvosanat";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);

        assertNotNull(driver.findElement(By.id("gradegrid-table")));

    }

    @Test
    public void testAddLanguage() {
        final String url = getBaseUrl() + "/" + "lomake/Yhteishaku/yhteishaku/arvosanat";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);

        driver.findElement(By.id("add_language_button")).click();

        assertNotNull(driver.findElement(By.className("gradegrid-custom-language-row")));
    }
}
