package fi.vm.sade.haku;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionTimeoutCookieFilter implements Filter {

    private final String SESSION_EXPIRES_COOKIE_NAME = "sessionExpiresInMillis";
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpSession session = ((HttpServletRequest)request).getSession(false);

        if(null != session) {

            long expiration = session.getLastAccessedTime() + (1000 * session.getMaxInactiveInterval());
            long counter = expiration - System.currentTimeMillis();
            setCookie(response, SESSION_EXPIRES_COOKIE_NAME, "" + counter);
            
        }
        
        filterChain.doFilter(request, response);
    }
    
    private void setCookie(ServletResponse response, String name, String value) {
        Cookie expiresCookie = new Cookie(name, value);
        expiresCookie.setPath("/");
        ((HttpServletResponse)response).addCookie(expiresCookie);
    }

    @Override
    public void destroy() {
        
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }
}
