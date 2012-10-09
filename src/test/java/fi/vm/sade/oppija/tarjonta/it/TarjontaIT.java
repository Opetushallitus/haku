package fi.vm.sade.oppija.tarjonta.it;

import fi.vm.sade.oppija.haku.it.TomcatContainerTest;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author jukka
 * @version 10/8/122:46 PM}
 * @since 1.1
 */
public class TarjontaIT extends TomcatContainerTest {

    protected void initModel() throws IOException {
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
        setBaseUrl(getBaseUrl());
        beginAt("add.html");
        ClassPathResource classPathResource = new ClassPathResource("tarjontatieto-test-data.xml");
        String absolutePath = classPathResource.getFile().getAbsolutePath();
        setTextField("upfile", absolutePath);
        submit("Lataa");
    }


    @Test
    public void testTarjonta() throws Exception {
        initModel();
        setScriptingEnabled(false);
        beginAt("/fi/tarjontatiedot");
        setTextField("text", "perustutkinto");
        submit();
        assertLinkPresentWithExactText("Liikunnanohjauksen perustutkinto");
    }
     @Test
    public void testTarjontaLink() throws Exception {
        initModel();
        setScriptingEnabled(false);
        beginAt("/fi/tarjontatiedot/1");
        assertLinkPresentWithExactText("Hae");
    }
}
