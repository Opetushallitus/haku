package fi.vm.sade.oppija.lomake.it;

import fi.vm.sade.oppija.lomake.FormModelHelper;
import fi.vm.sade.oppija.lomake.converter.FormModelToJsonString;
import fi.vm.sade.oppija.lomake.domain.FormModel;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 9/13/123:42 PM}
 * @since 1.1
 */
public abstract class AbstractRemoteTest extends TomcatContainerBase {

    public AbstractRemoteTest() {
    }

    protected FormModelHelper initModel(FormModel formModel1) {
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
        setBaseUrl(getBaseUrl());
        beginAt("/admin/edit");
        login("admin");
        gotoPage("/admin/edit");
        final String convert = new FormModelToJsonString().convert(formModel1);
        setTextField("model", convert);
        submit("tallenna");
        return new FormModelHelper(formModel1);
    }

}
