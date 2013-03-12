package fi.vm.sade.oppija.lomake.validation.validators.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnknownParameterNameTest {

    public static final String MESSAGE = "message";

    @Test
    public void testMessage() throws Exception {
        UnknownParameterName unknownParameterName = new UnknownParameterName(MESSAGE);
        assertEquals("Wrong exception message", MESSAGE, unknownParameterName.getMessage());

    }
}
