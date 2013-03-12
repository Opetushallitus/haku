package fi.vm.sade.oppija.lomake.domain.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationExceptionTest {
    @Test
    public void testCause() throws Exception {
        Exception expectedCause = new Exception();
        ConfigurationException configurationException = new ConfigurationException(expectedCause);
        assertEquals(expectedCause, configurationException.getCause());

    }
}
