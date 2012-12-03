package fi.vm.sade.oppija.common.selenium;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.lomake.converter.FormModelToJsonString;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import fi.vm.sade.oppija.haku.selenium.PageObject;
import fi.vm.sade.oppija.haku.selenium.SeleniumHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author jukka
 * @version 10/15/123:47 PM}
 * @since 1.1
 */
public class AdminEditPage extends LoginPage implements PageObject {
    private final String baseUrl;
    private final Selenium selenium;
    private WebDriver driver;

    public AdminEditPage(String baseUrl, SeleniumHelper selenium) {
        super(selenium.getSelenium());
        this.baseUrl = baseUrl;
        this.selenium = selenium.getSelenium();
        this.driver = selenium.getDriver();
    }


    @Override
    public String getUrl() {
        return baseUrl + "/admin/edit";
    }


    public void submitForm(FormModel formModel1) {
        final String convert = new FormModelToJsonString().convert(formModel1);
        final WebElement model = driver.findElement(By.id("model"));
        model.clear();
        selenium.runScript("document.getElementById('model').value='" + convert + "'");
        // model.sendKeys(convert);
        final WebElement tallenna = driver.findElement(By.id("tallenna"));
        tallenna.click();
    }
}
