package fi.vm.sade.haku.util;

import fi.vm.sade.hakutest.AuthedIntegrationTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.ws.rs.core.HttpHeaders;

public class ThreadLocalStateForTesting {
    public static void init() {
        initRequestContextHolder();
        AuthedIntegrationTest.classSetup();
    }

    public static void reset() {
        AuthedIntegrationTest.classTearDown();
        RequestContextHolder.resetRequestAttributes();
    }

    private static void initRequestContextHolder() {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader(HttpHeaders.USER_AGENT, ThreadLocalStateForTesting.class.getName());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest));
    }
}
