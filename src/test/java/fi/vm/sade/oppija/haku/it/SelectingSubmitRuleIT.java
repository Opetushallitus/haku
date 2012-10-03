package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.questions.Radio;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import fi.vm.sade.oppija.haku.domain.rules.SelectingSubmitRule;
import org.junit.Before;

import java.io.IOException;

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
        SelectingSubmitRule selectingSubmitRule = new SelectingSubmitRule("foo", "\\d{6}\\S\\d{2}[13579]\\w", child.getOptions().get(0));
        selectingSubmitRule.addChild(child);
        textQuestion.addChild(selectingSubmitRule);


        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(textQuestion);
        this.formModelHelper = initModel(formModel);
    }
}
