package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.dao.impl.FormModelDummyMemoryDaoImpl;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author Hannu Lyytikainen
 */
public class GradeGridIT extends AbstractRemoteTest {

    private FormModelHelper formModelHelper;

    @Before
    public void init() {
        FormModelDummyMemoryDaoImpl dummyMem = new FormModelDummyMemoryDaoImpl();
        FormModel formModel = new FormModelBuilder().buildDefaultFormWithFields(dummyMem.createGradeGrid());

        this.formModelHelper = initModel(formModel);
    }

    @Test
    public void testTableExists() {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);
        assertTablePresent("gradegrid-table");
    }

    @Test
    @Ignore
    public void testAddLanguage() {
        final String startUrl = formModelHelper.getStartUrl();
        beginAt(startUrl);

        setScriptingEnabled(true);

        clickButton("add_language_button");

        assertElementPresentByXPath("//*[@class='gradegrid-custom-language-row']");
    }



}
