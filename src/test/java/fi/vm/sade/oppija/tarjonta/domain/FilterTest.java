package fi.vm.sade.oppija.tarjonta.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class FilterTest {

    public static final String EXPECTED_NAME = "name";
    public static final ArrayList<FilterValue> EXPECTED_FILTER_VALUES = new ArrayList<FilterValue>();
    private Filter filter;

    @Before
    public void setUp() throws Exception {
        filter = new Filter(EXPECTED_NAME, EXPECTED_FILTER_VALUES);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(EXPECTED_NAME, filter.getName());
    }

    @Test
    public void testGetFilterValues() throws Exception {
        assertEquals(EXPECTED_FILTER_VALUES, filter.getFilterValues());
    }

    @Test
    public void testGetFilterValuesSize() throws Exception {
        List<FilterValue> filterValues = filter.getFilterValues();
        assertEquals(0, filterValues.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testValuesModification() throws Exception {
        List<FilterValue> filterValues = filter.getFilterValues();
        filterValues.add(new FilterValue("test"));
    }
}
