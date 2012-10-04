package fi.vm.sade.oppija.tarjonta.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class FilterValueTest {

    public static final String EXPECTED_NAME = "name";
    public static final String EXPECTED_LABEL = "label";
    private FilterValue filterValue = new FilterValue(EXPECTED_NAME, EXPECTED_LABEL);

    @Test
    public void testGetName() throws Exception {
        assertEquals(EXPECTED_NAME, filterValue.getName());
    }

    @Test
    public void testGetLabel() throws Exception {
        assertEquals(EXPECTED_LABEL, filterValue.getLabel());
    }
}
