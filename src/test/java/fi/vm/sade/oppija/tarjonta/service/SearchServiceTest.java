package fi.vm.sade.oppija.tarjonta.service;

import fi.vm.sade.oppija.tarjonta.domain.SearchParameters;
import fi.vm.sade.oppija.tarjonta.domain.SearchResult;
import fi.vm.sade.oppija.tarjonta.domain.exception.SearchException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author jukka
 * @version 10/8/123:44 PM}
 * @since 1.1
 */
public class SearchServiceTest {
    @Test
    public void testSearch() throws Exception {
        final SearchResult search = new MockSearchService().search(new SearchParameters(new HashMap<String, Map<String, String>>()));
        assertEquals(0, search.getSize());
    }

    @Test
    public void testSearchById() throws Exception {
        final Map<String, Object> stringObjectMap = new MockSearchService().searchById(new SearchParameters(new HashMap<String, Map<String, String>>()));
        assertEquals(0, stringObjectMap.size());
    }

    @Test
    public void testGetUniqValuesByField() throws Exception {
        final Collection<String> foo = new MockSearchService().getUniqValuesByField("foo");
        assertEquals(0, foo.size());
    }

    private class MockSearchService implements SearchService {

        @Override
        public SearchResult search(SearchParameters searchParameters) throws SearchException {
            return new SearchResult(new ArrayList<Map<String, Object>>());
        }

        @Override
        public Map<String, Object> searchById(SearchParameters searchParameters) {
            return new HashMap<String, Object>();
        }

        @Override
        public Collection<String> getUniqValuesByField(String field) {
            return new ArrayList<String>();
        }
    }
}
