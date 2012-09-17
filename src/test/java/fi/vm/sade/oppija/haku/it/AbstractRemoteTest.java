package fi.vm.sade.oppija.haku.it;

import fi.vm.sade.oppija.haku.converter.FormModelToJsonString;
import fi.vm.sade.oppija.haku.domain.FormModel;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;
import org.junit.Before;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/13/123:42 PM}
 * @since 1.1
 */
public abstract class AbstractRemoteTest {

    protected FormModel formModel;

    @Before
    public void init() throws IOException {
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
        setBaseUrl("http://localhost:8080/haku");
        beginAt("/fi/admin/edit");
        final String convert = new FormModelToJsonString().convert(formModel);
        setTextField("model", convert);
        submit("tallenna");
    }
}
