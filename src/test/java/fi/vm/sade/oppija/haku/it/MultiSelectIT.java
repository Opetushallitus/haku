package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.questions.MultiSelect;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/18/122:03 PM}
 * @since 1.1
 */
public class MultiSelectIT extends AbstractRemoteTest {

    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final MultiSelect multiSelect = new MultiSelect("multiselect", "foo");
        multiSelect.addOption("value1", "multiselect", "title");
        multiSelect.addOption("value2", "multiselect2", "title2");
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(multiSelect);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        dumpHtml();
        assertElementPresent("multiselect");
        final IElement multiselect = getElementById("multiselect");
        assertEquals(2, multiselect.getChildren().size());
        assertEquals("select", multiselect.getName());
        final IElement elementById = getElementById("multiselect.value1");
        assertEquals("option", elementById.getName());
    }
}
