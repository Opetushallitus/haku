package fi.vm.sade.oppija.ui.selenium;

import clover.com.lowagie.text.pdf.BadPdfFormatException;
import fi.vm.sade.oppija.common.selenium.AbstractSeleniumBase;
import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.lomake.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.lomake.domain.elements.questions.Radio;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author Hannu Lyytikainen
 */
public class EducationBackgroundTest extends AbstractSeleniumBase {

    private FormModelHelper formModelHelper;

    @Before
    public void init() {
        FormModelDummyMemoryDaoImpl formModel = new FormModelDummyMemoryDaoImpl();
        this.formModelHelper = initModel(formModel.getModel());

    }

    @Test
    public void testRule() {
        final String startUrl = formModelHelper.getFormUrl(formModelHelper.getFirstForm().getPhase("koulutustausta"));

        WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + startUrl); //  lomake/Yhteishaku/yhteishaku/henkilotiedot

        driver.findElement(new By.ById("millatutkinnolla_tutkinto1")).click();

        driver.findElement(new By.ById("peruskoulu2012_ei")).click();

        driver.findElement(new By.ByName("paattotodistusvuosi"));

        driver.findElement(new By.ById("millatutkinnolla_tutkinto6")).click();

        driver.findElement(new By.ByName("lukiovuosi"));

    }

}
