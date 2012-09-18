package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.questions.CheckBox;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/18/128:54 AM}
 * @since 1.1
 */
public class CheckBoxIT extends AbstractRemoteTest {
    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final CheckBox checkBox = new CheckBox("checkbox", "foo");
        checkBox.addOption("checkbox_value", "value", "title");
        checkBox.addOption("checkbox_value2", "value2", "title2");
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(checkBox);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        dumpHtml();
        assertElementPresent("checkbox.checkbox_value");
        assertElementPresent("checkbox.checkbox_value2");
        final IElement elementById = getElementById("checkbox.checkbox_value");
        assertEquals("input", elementById.getName());
        assertEquals("checkbox.checkbox_value", elementById.getAttribute("name"));
    }
}
