package fi.vm.sade.haku.oppija.lomake.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationExceptionTest {

    public static final String MESSAGE = "message";

    @Test
    public void testCause() throws Exception {
        Exception expectedCause = new Exception();
        ConfigurationException configurationException = new ConfigurationException(expectedCause);
        assertEquals(expectedCause, configurationException.getCause());
    }

    @Test
    public void testCauseAndMessage() throws Exception {
        Exception expectedCause = new Exception();
        ConfigurationException configurationException = new ConfigurationException(MESSAGE, expectedCause);
        assertEquals(expectedCause, configurationException.getCause());
        assertEquals(MESSAGE, configurationException.getMessage());
    }
}
