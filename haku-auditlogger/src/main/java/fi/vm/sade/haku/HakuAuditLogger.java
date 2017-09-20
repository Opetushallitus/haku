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
        if(sra != null) {
            HttpServletRequest req = sra.getRequest();
            String sessionId = req.getSession().getId();
            String useragent = req.getHeader("User-Agent");
            String remoteAddr = req.getRemoteAddr();
            try {
                InetAddress address = InetAddress.getByName(remoteAddr);
                return new User(getCurrentPersonOid(), address, sessionId, useragent);
            } catch (UnknownHostException e) {
                LOGGER.error("Error creating inetadress for user out of {}, returning null user", remoteAddr, e);
                return null;
            }
        } else {
            try {
                return new User(getCurrentPersonOid(), InetAddress.getLocalHost(), "", "");
            } catch (UnknownHostException e) {
                LOGGER.error("Error creating localhost inetaddress",e);
                return null;
            }
        }
    }
}
