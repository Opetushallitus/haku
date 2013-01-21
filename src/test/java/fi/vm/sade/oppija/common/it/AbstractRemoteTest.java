package fi.vm.sade.oppija.common.it;

import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import static net.sourceforge.jwebunit.junit.JWebUnit.setTestingEngineKey;

/**
 * @author Hannu Lyytikainen
 */
public abstract class AbstractRemoteTest extends TomcatContainerBase  {

    public void initTestEngine() {
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
    }

}
