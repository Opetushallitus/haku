package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.FormModelHelper;
import fi.vm.sade.oppija.haku.domain.FormModel;
import fi.vm.sade.oppija.haku.domain.builders.FormModelBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;

/**
 * @author Mikko Majapuro
 */
public class AutoCompleteIT extends AbstractRemoteTest {

    @Before
    public void init() throws IOException {
        FormModel formModel = new FormModelBuilder().withDefaults().build();
        initModel(formModel);
    }

    @Test
    public void testOneMatch() throws IOException {
        beginAt("fi/education/institute/search?term=koulu100");
        assertTextPresent("[{\"id\":\"100\",\"name\":\"Koulu100\",\"key\":\"koulu100\"}]");
    }

    @Test
    public void testNoMatch() throws IOException {
        beginAt("fi/education/institute/search?term=xyz");
        assertTextPresent("[]");
    }

    @Test
    public void testEmptySearchTerm() throws IOException {
        beginAt("fi/education/institute/search?term=");
        assertTextPresent("[]");
    }

    @Test
    public void testMultipleMatch() throws IOException {
        beginAt("fi/education/institute/search?term=koulu1");
        assertTextPresent("koulu1");
        assertTextPresent("koulu10");
        assertTextPresent("koulu11");
        assertTextPresent("koulu12");
        assertTextPresent("koulu13");
        assertTextPresent("koulu14");
        assertTextPresent("koulu15");
        assertTextPresent("koulu16");
        assertTextPresent("koulu17");
        assertTextPresent("koulu18");
    }

}
