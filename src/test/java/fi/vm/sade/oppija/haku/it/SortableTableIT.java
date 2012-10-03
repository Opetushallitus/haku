package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import fi.vm.sade.oppija.haku.domain.elements.custom.PreferenceRow;
import fi.vm.sade.oppija.haku.domain.elements.custom.SortableTable;
import net.sourceforge.jwebunit.api.IElement;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertElementPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.getElementById;

/**
 * Sortable table integration tests
 *
 * @author Mikko Majapuro
 */
public class SortableTableIT extends AbstractRemoteTest {

    private FormModelHelper formModelHelper;

    @Before
    public void init() throws IOException {
        final SortableTable table = new SortableTable("t1", "Hakutoiveet", "Ylös", "Alas");
        final PreferenceRow row = new PreferenceRow("r1", "Hakutoive 1", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        final PreferenceRow row2 = new PreferenceRow("r2", "Hakutoive 2", "Tyhjennä", "Koulutus", "Opetuspiste", "Valitse koulutus");
        row.addOption("1", "autoala", "Autoala");
        row.addOption("2", "ruotsi", "Ruotsi");
        row2.addOption("1", "englanti", "Englanti");
        row2.addOption("2", "matematiikka", "Matematiikka");
        table.addChild(row);
        table.addChild(row2);
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(table);
        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testSelectInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        assertElementPresent("r1-Koulutus");
        final IElement select = getElementById("r1-Koulutus");
        assertEquals(3, select.getChildren().size());
        assertEquals("select", select.getName());
        final IElement elementById = getElementById("r1_1");
        assertEquals("option", elementById.getName());

        assertElementPresent("r2-Koulutus");
        final IElement select2 = getElementById("r2-Koulutus");
        assertEquals(3, select2.getChildren().size());
        assertEquals("select", select.getName());
        final IElement elementById2 = getElementById("r2_2");
        assertEquals("option", elementById2.getName());
    }

    @Test
    public void testTextInputExists() throws IOException {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        assertElementPresent("r1-Opetuspiste");
        final IElement input = getElementById("r1-Opetuspiste");
        assertEquals("input", input.getName());
        assertEquals("text", input.getAttribute("type"));

        assertElementPresent("r2-Opetuspiste");
        final IElement input2 = getElementById("r2-Opetuspiste");
        assertEquals("input", input2.getName());
        assertEquals("text", input2.getAttribute("type"));
    }
}
