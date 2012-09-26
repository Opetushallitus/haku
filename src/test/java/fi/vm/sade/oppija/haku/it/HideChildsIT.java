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
import org.junit.Test;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/20/123:26 PM}
 * @since 1.1
 */
public class HideChildsIT extends AbstractRemoteTest {

    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final CheckBox checkBox = new CheckBox("checkbox", "foo");
        checkBox.addOption("value", "value", "title");
        checkBox.addOption("value2", "value2", "title2");

        final Option option = checkBox.getOptions().get(0);

        final QuestionGroup questionGroup = new QuestionGroup("ekaryhma", "ekaryhma");
        questionGroup.addChild(new TextQuestion("alikysymys1", "alikysymys1"));
        questionGroup.addChild(new TextQuestion("alikysymys2", "alikysymys2"));

        final EnablingSubmitRule enablingSubmitRule = new EnablingSubmitRule(option.getId());
        enablingSubmitRule.setRelated(option, questionGroup);

        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(checkBox, enablingSubmitRule);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testInputExistsNoJavaScript() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        // setScriptingEnabled(false);
        dumpHtml();
        assertElementPresent("checkbox_value");
        assertElementPresent("checkbox_value2");
        assertElementNotPresent("ekaryhma");
        assertElementNotPresent("alikysymys1");
        // assertButtonPresent("rule-enabled-checkbox.checkbox_value");
        //clickButton("rule-enabled-checkbox.checkbox_value");
        checkCheckbox("checkbox_value");
        dumpHtml();
        assertElementPresent("alikysymys1");

    }
}
