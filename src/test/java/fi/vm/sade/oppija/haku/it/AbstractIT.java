package fi.vm.sade.oppija.haku.it;


import net.sourceforge.jwebunit.util.TestingEngineRegistry;
import org.junit.Before;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author Hannu Lyytikainen
 */

public abstract class AbstractIT {


    protected String jsonModelFileName;

    @Before
    public void init() throws IOException {
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
        setBaseUrl("http://localhost:8080/haku");
        beginAt("/fi/admin");
        ClassPathResource classPathResource = new ClassPathResource(jsonModelFileName);
        String absolutePath = classPathResource.getFile().getAbsolutePath();
        setTextField("file", absolutePath);
        submit("tallenna");
    }
}
