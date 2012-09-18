package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.questions.DropdownSelect;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/18/122:13 PM}
 * @since 1.1
 */
public class DropdownSelectIT extends AbstractRemoteTest {

    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final DropdownSelect select = new DropdownSelect("select", "foo");
        select.addOption("value1", "select", "title");
        select.addOption("value2", "select2", "title2");
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(select);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        assertElementPresent("select");
        final IElement select = getElementById("select");
        assertEquals(2, select.getChildren().size());
        assertEquals("select", select.getName());
        final IElement elementById = getElementById("select.value1");
        assertEquals("option", elementById.getName());
    }
}
