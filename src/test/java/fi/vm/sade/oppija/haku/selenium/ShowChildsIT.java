package fi.vm.sade.oppija.haku.selenium;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.elements.Teema;
import fi.vm.sade.oppija.haku.domain.questions.CheckBox;
import fi.vm.sade.oppija.haku.domain.questions.Option;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import fi.vm.sade.oppija.haku.domain.rules.EnablingSubmitRule;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;


/**
 * @author jukka
 * @version 9/20/123:26 PM}
 * @since 1.1
 */
public class ShowChildsIT extends AbstractSeleniumBase {

    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final CheckBox checkBox = new CheckBox("checkbox", "Valitse jotain");
        checkBox.addOption("value", "value", "title");
        checkBox.addOption("value2", "value2", "title2");

        final Option option = checkBox.getOptions().get(0);
        final Option option2 = checkBox.getOptions().get(1);

        final Teema teema = new Teema("ekaryhma", "ekaryhma", null);
        teema.addChild(new TextQuestion("alikysymys1", "alikysymys1"));
        teema.addChild(new TextQuestion("alikysymys2", "alikysymys2"));

        final EnablingSubmitRule enablingSubmitRule = new EnablingSubmitRule(option.getId(), ".*");
        enablingSubmitRule.setRelated(option, teema);
        option.addChild(enablingSubmitRule);

        final EnablingSubmitRule enablingSubmitRule2 = new EnablingSubmitRule(option2.getId(), ".*");
        final TextQuestion textQuestion = new TextQuestion("laitakolmenollaa", "Laita kolme nollaa tähän");
        enablingSubmitRule2.setRelated(option2, textQuestion);
        option2.addChild(enablingSubmitRule2);

        final EnablingSubmitRule enablingSubmitRule3 = new EnablingSubmitRule(textQuestion.getId(), "[0]{3}");
        enablingSubmitRule3.setRelated(textQuestion, new TextQuestion("tamanakyykolmellanollalla", "tamanakyykolmellanollalla"));
        textQuestion.addChild(enablingSubmitRule3);

        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(checkBox);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testInputExistsWithJavaScript() throws IOException, InterruptedException {
        final String startUrl = formModelHelper.getStartUrl();
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + startUrl);
        driver.findElement(By.id("checkbox_value"));
        driver.findElement(By.id("checkbox_value2"));
        seleniumHelper.getSelenium().click("checkbox_value");
        final WebElement alikysymys1 = driver.findElement(By.id("alikysymys1"));
        assertNotNull(alikysymys1);
    }
}
