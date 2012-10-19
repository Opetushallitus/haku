package fi.vm.sade.oppija.haku.selenium;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author jukka
 * @version 10/15/123:25 PM}
 * @since 1.1
 */
public class HautKoulutuksiinTest extends AbstractSeleniumBase {

    private FormModelHelper formModelHelper;

    @Test
    public void testSaveHakemusAndList() {
        buildFormWithOneQuestion();
        fillForm();
        loginAsNormalUser();
        assertTrue(weAreAtAjankohtaisetHakemukset());
        assertTrue(hakemusListIncludesFilledForm());
    }

    private boolean hakemusListIncludesFilledForm() {
        return seleniumHelper.getSelenium().isTextPresent(formModelHelper.getFirstForm().getTitle());
    }

    private boolean weAreAtAjankohtaisetHakemukset() {
        return seleniumHelper.getSelenium().isTextPresent("Ajankohtaiset hakemukset");
    }

    private void loginAsNormalUser() {
        new HautKoulutuksiinPage(getBaseUrl(), seleniumHelper).login();
    }

    private void fillForm() {
        seleniumHelper.getDriver().get(getBaseUrl() + "/" + formModelHelper.getStartUrl());
        seleniumHelper.getSelenium().type("eka", "arvo");
        seleniumHelper.getSelenium().click("nav-save");
    }

    private void buildFormWithOneQuestion() {
        final FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(new TextQuestion("eka", "kysymys"));
        formModelHelper = initModel(formModel);
    }


}
