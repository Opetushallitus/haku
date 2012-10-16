package fi.vm.sade.oppija.haku.selenium;

import com.thoughtworks.selenium.Selenium;
import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.ApplicationPeriod;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.Hakukohde;
import fi.vm.sade.oppija.haku.domain.Organisaatio;
import fi.vm.sade.oppija.haku.domain.elements.Form;
import fi.vm.sade.oppija.haku.domain.elements.Teema;
import fi.vm.sade.oppija.haku.domain.elements.Vaihe;
import fi.vm.sade.oppija.haku.domain.elements.custom.PreferenceRow;
import fi.vm.sade.oppija.haku.domain.elements.custom.SortableTable;
import fi.vm.sade.oppija.haku.domain.questions.Question;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test for education institute preferences
 * @author Mikko Majapuro
 */
public class HakutoiveetTest extends AbstractSeleniumTest {

    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        ApplicationPeriod applicationPeriod = new ApplicationPeriod("test");
        FormModel formModel = new FormModel();
        formModel.addApplicationPeriod(applicationPeriod);
        Vaihe hakutoiveet = new Vaihe("hakutoiveet", "Hakutoiveet");
        Form form = new Form("lomake", "yhteishaku");
        form.addChild(hakutoiveet);
        form.init();

        Map<String, List<Question>> lisakysymysMap = new HashMap<String, List<Question>>();

        int AMOUNT_OF_TEST_OPETUSPISTE = 1;
        int AMOUNT_OF_TEST_HAKUKOHDE = 1;
        List<Organisaatio> institutes = new ArrayList<Organisaatio>();

        for (int i = 0; i < AMOUNT_OF_TEST_OPETUSPISTE; ++i) {
            Organisaatio op = new Organisaatio(String.valueOf(i), "Koulu" + i);
            institutes.add(op);
        }

        for (Organisaatio institute : institutes) {
            List<Hakukohde> hakukohdeList = new ArrayList<Hakukohde>();
            for (int i = 0; i < AMOUNT_OF_TEST_HAKUKOHDE; i++) {
                String id = String.valueOf(institute.getId()) + "_" + String.valueOf(i);
                TextQuestion textQuestion = new TextQuestion(id + "_additional_question_1", "Lorem ipsum");
                List<Question> lisakysymysList = new ArrayList<Question>();
                lisakysymysList.add(textQuestion);
                lisakysymysMap.put(id, lisakysymysList);
            }
        }
        applicationPeriod.addForm(form);

        Teema hakutoiveetRyhmä = new Teema("hakutoiveetGrp", "Hakutoiveet", lisakysymysMap);
        hakutoiveet.addChild(hakutoiveetRyhmä);
        hakutoiveetRyhmä.setHelp("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.");
        SortableTable sortableTable = new SortableTable("preferencelist", "Hakutoiveet", "Ylös", "Alas");
        PreferenceRow pr1 = new PreferenceRow("preference1", "Hakutoive 1", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        PreferenceRow pr2 = new PreferenceRow("preference2", "Hakutoive 2", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        PreferenceRow pr3 = new PreferenceRow("preference3", "Hakutoive 3", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        sortableTable.addChild(pr1);
        sortableTable.addChild(pr2);
        sortableTable.addChild(pr3);
        hakutoiveetRyhmä.addChild(sortableTable);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testEducationPreference() throws InterruptedException {
        final String url = "lomake/test/lomake/hakutoiveet";
        final WebDriver driver = seleniumHelper.getDriver();
        driver.get(getBaseUrl() + "/" + url);
        driver.findElement(By.id("preference1-Opetuspiste"));
        Selenium s = seleniumHelper.getSelenium();
        s.typeKeys("preference1-Opetuspiste", "koulu");
        WebDriverWait wait = new WebDriverWait(driver, 5, 1000);
        wait.until(new ExpectedCondition<WebElement>(){
            @Override
            public WebElement apply(WebDriver d) {
                return d.findElement(By.linkText("Koulu0"));
            }});
        driver.findElement(By.linkText("Koulu0")).click();
        wait.until(new ExpectedCondition<WebElement>(){
            @Override
            public WebElement apply(WebDriver d) {
                return d.findElement(By.xpath("//option[@value='0_0']"));
            }});
        driver.findElement(By.xpath("//option[@value='0_0']")).click();
        wait.until(new ExpectedCondition<WebElement>(){
            @Override
            public WebElement apply(WebDriver d) {
                return d.findElement(By.id("0_0_additional_question_1"));
            }});
    }
}
