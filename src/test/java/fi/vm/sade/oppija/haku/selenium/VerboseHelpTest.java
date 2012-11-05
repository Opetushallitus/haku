package fi.vm.sade.oppija.haku.selenium;

import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author hannu
 */
public class VerboseHelpTest extends AbstractSeleniumBase {

    @Before
    public void init() {
        FormModelDummyMemoryDaoImpl dummyMem = new FormModelDummyMemoryDaoImpl();
        initModel(dummyMem.getModel());
    }

    @Test
    public void testVerboseHelp() {
        final String url = getBaseUrl() + "/" + "lomake/test/yhteishaku/henkilotiedot/henkilotiedotGrp/help";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(url);
        assertNotNull("Could not find verbose help page", driver.findElement(By.id("help-page")));
    }
}
