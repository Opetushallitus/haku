package fi.vm.sade.oppija.haku.service;

import fi.vm.sade.oppija.tarjonta.domain.SearchParameters;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SearchParametersTest {

    public static final String SEARCH_FIELD = "text";
    public static final String TERM = "term";
    public static final String ORDER = "order";
    public static final String SORT_FIELD = "sortField";
    public static final Integer START = new Integer(0);
    public static final Integer ROWS = new Integer(10);
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String[] FIELDS = new String[]{ID, NAME};
    private SearchParameters searchParameters;

    @Before
    public void setUp() throws Exception {
        searchParameters = new SearchParameters(SEARCH_FIELD, TERM, ORDER, SORT_FIELD, START, ROWS, FIELDS);
    }

    @Test
    public void testGetTerm() throws Exception {
        assertEquals(TERM, searchParameters.getTerm());
    }

    @Test
    public void testOrder() throws Exception {
        assertEquals(ORDER, searchParameters.getSortOrder());
    }

    @Test
    public void testSortField() throws Exception {
        assertEquals(SORT_FIELD, searchParameters.getSortField());
    }

    @Test
    public void testStart() throws Exception {
        assertEquals(START, searchParameters.getStart());
    }

    @Test
    public void testRows() throws Exception {
        assertEquals(ROWS, searchParameters.getRows());
    }
}
