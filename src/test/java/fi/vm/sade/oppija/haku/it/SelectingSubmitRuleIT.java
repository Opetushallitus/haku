package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.questions.Radio;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import fi.vm.sade.oppija.haku.domain.rules.SelectingSubmitRule;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 10/3/123:25 PM}
 * @since 1.1
 */
public class SelectingSubmitRuleIT extends AbstractRemoteTest {
    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final TextQuestion textQuestion = new TextQuestion("hetu", "Henkilötunnus");

        final Radio child = new Radio("sukupuoli", "sukupuoli");
        child.addOption("mies", "mies", "Mies");
        child.addOption("nainen", "nainen", "Nainen");
        SelectingSubmitRule selectingSubmitRule = new SelectingSubmitRule(textQuestion.getId(), child.getId());
        selectingSubmitRule.addBinding(textQuestion, child, "\\d{6}\\S\\d{2}[13579]\\w", child.getOptions().get(0));
        selectingSubmitRule.addBinding(textQuestion, child, "\\d{6}\\S\\d{2}[24680]\\w", child.getOptions().get(1));


        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(selectingSubmitRule);
        this.formModelHelper = initModel(formModel);
    }


    @Test
    public void testInputMale() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        setScriptingEnabled(false);
        assertElementPresent("hetu");
        assertElementNotPresent("sukupuoli");
        setTextField("hetu", "010101-111X");
        dumpHtml();
        assertElementPresent("sukupuoli_mies");
        submit();
        assertRadioOptionSelected("sukupuoli", "mies");
    }
}
