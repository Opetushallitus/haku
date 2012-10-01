package fi.vm.sade.oppija.haku.controller;

import fi.vm.sade.oppija.tarjonta.controller.SearchController;
import fi.vm.sade.oppija.tarjonta.domain.SearchParameters;
import fi.vm.sade.oppija.tarjonta.domain.SearchResult;
import fi.vm.sade.oppija.tarjonta.domain.exception.SearchException;
import fi.vm.sade.oppija.tarjonta.service.SearchService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static fi.vm.sade.oppija.haku.service.SearchParametersTest.*;
import static org.junit.Assert.assertEquals;

public class SearchControllerTest {

    public static final String[] FIELDS = new String[]{"id", "name"};

    public final SearchParameters SEARCH_TERM = new SearchParameters(SEARCH_FIELD, TERM, ORDER, SORT_FIELD, START, ROWS, FIELDS);

    private SearchController searchController;
    private SearchResult searchResult;

    @Before
    public void setUp() throws Exception {
        searchController = new SearchController(new SearchService() {
            @Override
            public SearchResult search(SearchParameters searchParameters) throws SearchException {
                searchResult = new SearchResult(new ArrayList<Map<String, Object>>());
                return searchResult;
            }

            @Override
            public Map<String, Object> searchById(SearchParameters searchParameters) {
                Map<String, Object> result = new HashMap<String, Object>();
                return result;
            }

        });
    }

    @Test
    public void testListTarjontatiedotViewName() throws Exception {
        ModelAndView modelAndView = searchController.listTarjontatiedot(SEARCH_TERM);
        assertEquals(modelAndView.getViewName(), SearchController.VIEW_NAME_ITEMS);
    }

    @Test
    public void testListTarjontatiedotModel() throws Exception {
        ModelAndView modelAndView = searchController.listTarjontatiedot(SEARCH_TERM);
        assertEquals(modelAndView.getModel().get(SearchController.MODEL_NAME), searchResult);
    }
}
