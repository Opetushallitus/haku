package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.elements.QuestionGroup;
import fi.vm.sade.oppija.haku.domain.questions.CheckBox;
import fi.vm.sade.oppija.haku.domain.questions.Option;
import fi.vm.sade.oppija.haku.domain.questions.TextQuestion;
import fi.vm.sade.oppija.haku.domain.rules.EnablingSubmitRule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/20/123:26 PM}
 * @since 1.1
 */
@Ignore
public class ShowChildsIT extends AbstractRemoteTest {

    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final CheckBox checkBox = new CheckBox("checkbox", "Valitse jotain");
        checkBox.addOption("value", "value", "title");
        checkBox.addOption("value2", "value2", "title2");

        final Option option = checkBox.getOptions().get(0);
        final Option option2 = checkBox.getOptions().get(1);

        final QuestionGroup questionGroup = new QuestionGroup("ekaryhma", "ekaryhma");
        questionGroup.addChild(new TextQuestion("alikysymys1", "alikysymys1"));
        questionGroup.addChild(new TextQuestion("alikysymys2", "alikysymys2"));

        final EnablingSubmitRule enablingSubmitRule = new EnablingSubmitRule(option.getId(), ".*");
        enablingSubmitRule.setRelated(option, questionGroup);
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
    public void testInputExistsWithJavaScript() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        dumpHtml();
        assertElementPresent("checkbox_value");
        assertElementPresent("checkbox_value2");
        assertElementNotPresent("ekaryhma");
        assertElementNotPresent("alikysymys1");
        // assertButtonPresent("rule-enabled-checkbox.checkbox_value");
        checkCheckbox("checkbox_value");
        assertElementPresent("alikysymys1");

    }
}
