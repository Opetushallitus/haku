package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.questions.CheckBox;
import fi.vm.sade.oppija.haku.domain.questions.Option;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/18/128:54 AM}
 * @since 1.1
 */
public class CheckBoxIT extends AbstractRemoteTest {
    private FormModelHelper formModelHelper;
    private CheckBox checkBox;

    @Before
    public void init() throws IOException {
        checkBox = new CheckBox("checkbox", "foo");
        checkBox.addOption("checkbox_value", "value", "title");
        checkBox.addOption("checkbox_value2", "value2", "title2");
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(checkBox);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        List<Option> options = checkBox.getOptions();
        for (Option option : options) {
            assertElementPresent(option.getId());
        }
        final IElement elementById = getElementById("checkbox_checkbox_value");
        assertEquals("input", elementById.getName());
        assertEquals("checkbox_checkbox_value", elementById.getAttribute("name"));
    }
}
