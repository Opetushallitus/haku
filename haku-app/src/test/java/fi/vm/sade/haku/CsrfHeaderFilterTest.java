package fi.vm.sade.haku;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class CsrfHeaderFilterTest extends Assert {

    private final static String CSRF_COOKIE_VALUE = "cookie-value";
    private final static String CSRF_HEADER_OLD_VALUE = "old-value";
    private CsrfHeaderFilter csrfHeaderFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpServletRequest nextRequest;
    private FilterChain filterChain;

    @Before
    public void setup() {
        csrfHeaderFilter = new CsrfHeaderFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                nextRequest = (HttpServletRequest)request;
            }
        };
    }

    @Test
    public void requestHasNoCookieAtAll_DontAddHeader() throws IOException, ServletException {
        when(request.getCookies()).thenReturn(null);

        csrfHeaderFilter.doFilter(request, response, filterChain);

        assertNull(nextRequest.getHeader(CsrfHeaderFilter.CSRF_HEADER_NAME));
    }

    @Test
    public void requestHasNoCsrfCookie_DontAddHeader() throws IOException, ServletException {
        when(request.getCookies()).thenReturn(createCookies("irrelevant", "some value"));

        csrfHeaderFilter.doFilter(request, response, filterChain);

        assertNull(nextRequest.getHeader(CsrfHeaderFilter.CSRF_HEADER_NAME));
    }

    @Test
    public void requestHasCsrfCookieAndNoCsrfHeader_AddCsrfHeader() throws IOException, ServletException {
        when(request.getCookies()).thenReturn(createCookies(CsrfHeaderFilter.CSRF_HEADER_NAME, CSRF_COOKIE_VALUE));

        csrfHeaderFilter.doFilter(request, response, filterChain);

        assertEquals(CSRF_COOKIE_VALUE, nextRequest.getHeader(CsrfHeaderFilter.CSRF_HEADER_NAME));
    }

    @Test
    public void requestAlreadyHasCsrfHeader_KeepOldCsrfHeader() throws IOException, ServletException {
        when(request.getCookies()).thenReturn(createCookies(CsrfHeaderFilter.CSRF_HEADER_NAME, CSRF_COOKIE_VALUE));
        when(request.getHeader(CsrfHeaderFilter.CSRF_HEADER_NAME)).thenReturn(CSRF_HEADER_OLD_VALUE);

        csrfHeaderFilter.doFilter(request, response, filterChain);

        assertEquals("Still old value", CSRF_HEADER_OLD_VALUE, nextRequest.getHeader(CsrfHeaderFilter.CSRF_HEADER_NAME));
    }

    private Cookie[] createCookies(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        Cookie[] cookies = new Cookie[1];
        cookies[0] = cookie;
        return cookies;
    }
}