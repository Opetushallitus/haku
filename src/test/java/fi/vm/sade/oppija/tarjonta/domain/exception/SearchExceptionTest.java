package fi.vm.sade.oppija.tarjonta.domain.exception;

import org.junit.Test;

/**
 * @author jukka
 * @version 10/8/123:42 PM}
 * @since 1.1
 */
public class SearchExceptionTest {

    @Test(expected = SearchException.class)
    public void test() throws Exception {
        throw new SearchException("foo");
    }

    @Test(expected = SearchException.class)
    public void testExeption() throws Exception {
        throw new SearchException("foo", new RuntimeException("blaa"));
    }
}
