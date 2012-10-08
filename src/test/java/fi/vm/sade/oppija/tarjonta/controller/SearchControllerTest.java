package fi.vm.sade.oppija.tarjonta.controller;

import fi.vm.sade.oppija.tarjonta.domain.*;
import fi.vm.sade.oppija.tarjonta.domain.exception.SearchException;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchControllerTest {

    public static final String SORT_ORDER = "decs";
    public static final String SORT_FIELD = "name";
    private SearchController searchController;

    @Before
    public void setUp() throws Exception {
        SearchService searchService = createMockService();
        SearchFilters searchFilters = new SearchFilters(searchService);
        searchController = new SearchController(searchService, searchFilters);
    }

    @Test
    public void testGetSortParametersOrder() throws Exception {
        SortParameters sortParameters = searchController.getSortParameters(SORT_ORDER, SORT_FIELD);
        assertEquals(SORT_ORDER, sortParameters.getSortOrder());
    }
    @Test
    public void testGetSortParametersField() throws Exception {
        SortParameters sortParameters = searchController.getSortParameters(SORT_ORDER, SORT_FIELD);
        assertEquals(SORT_FIELD, sortParameters.getSortField());
    }
    @Test
    public void testGetPagingParametersStart() throws Exception {
        PagingParameters pagingParameters = searchController.getPagingParameters(1, 10);
        assertEquals(new Integer(1), pagingParameters.getStart());
    }
     @Test
    public void testGetPagingParametersCount() throws Exception {
        PagingParameters pagingParameters = searchController.getPagingParameters(1, 10);
        assertEquals(new Integer(10), pagingParameters.getRows());
    }

    @Test
    public void testGetFilters() throws Exception {
        Map<String, Map<String, String>> filters = searchController.getFilters(null, null, null, null);
        assertEquals(4, filters.size());
    }

    @Test
    public void testGetSearchParameters() throws Exception {
        SearchParameters searchParameters = searchController.getSearchParameters("text", null, null, searchController.getFilters(null, null, null, null));
        assertTrue(searchParameters.getFields().contains("id"));
        assertTrue(searchParameters.getFields().contains("name"));
    }

    private SearchService createMockService() {
        return new SearchService() {
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>(0);

            @Override
            public SearchResult search(SearchParameters searchParameters) throws SearchException {
                return new SearchResult(items);
            }

            @Override
            public Map<String, Object> searchById(SearchParameters searchParameters) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Collection<String> getUniqValuesByField(String field) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }
}
