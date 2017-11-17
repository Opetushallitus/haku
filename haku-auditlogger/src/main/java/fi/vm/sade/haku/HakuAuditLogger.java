package fi.vm.sade.haku;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.Logger;
import fi.vm.sade.auditlog.User;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Predicate;

public class HakuAuditLogger extends Audit {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HakuAuditLogger.class);

    protected HakuAuditLogger(Logger logger, String serviceName, ApplicationType applicationType) {
        super(logger, serviceName, applicationType);
    }

    private Oid getCurrentPersonOid() {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context == null) {
            return null;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        String personOid = auth.getName();
        try {
            return new Oid(personOid);
        } catch (GSSException e) {
            LOGGER.error("Error creating Oid-object out of {}", personOid);
            return null;
        }
    }

    public final User getUser() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Oid currentPersonOid = getCurrentPersonOid();
        if(sra != null) {
            InetAddress address;
            HttpServletRequest req = sra.getRequest();
            String sessionId = req.getSession().getId();
            String useragent = req.getHeader("User-Agent");
            String remoteAddr = getRemoteAddress(req);
            try {
                address = InetAddress.getByName(remoteAddr);
            } catch (UnknownHostException e) {
                LOGGER.error("Error creating inetadress for user out of {}, using loopback address", remoteAddr, e);
                address = InetAddress.getLoopbackAddress();
            }
            return new User(currentPersonOid, address, sessionId, useragent);
        } else {
            LOGGER.warn("Servlet request attributes not present, can not audit log all user details");
            return new User(currentPersonOid, InetAddress.getLoopbackAddress(), "", "");
        }
    }

    private String getRemoteAddress(HttpServletRequest httpServletRequest) {
        String xRealIp = httpServletRequest.getHeader("X-Real-IP");
        Predicate<String> isNotBlank = (String txt) -> txt != null && !txt.isEmpty();
        if (isNotBlank.test(xRealIp)) {
            return xRealIp;
        }
        String xForwardedFor = httpServletRequest.getHeader("X-Forwarded-For");
        if (isNotBlank.test(xForwardedFor)) {
            if (xForwardedFor.contains(",")) {
                LOGGER.error("Could not find X-Real-IP header, but X-Forwarded-For contains multiple values: {}, " +
                        "this can cause problems", xForwardedFor);
            }
            return xForwardedFor;
        }
        LOGGER.warn("X-Real-IP or X-Forwarded-For was not set. Are we not running behind a load balancer?");
        return httpServletRequest.getRemoteAddr();
    }
}
