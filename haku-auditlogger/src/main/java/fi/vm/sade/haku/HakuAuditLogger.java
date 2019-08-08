package fi.vm.sade.haku;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.Logger;
import fi.vm.sade.auditlog.User;
import fi.vm.sade.javautils.http.HttpServletRequestUtils;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HakuAuditLogger extends Audit {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HakuAuditLogger.class);
    private final boolean allowEmptyPersonOid;

    protected HakuAuditLogger(Logger logger,
                              String serviceName,
                              ApplicationType applicationType,
                              boolean allowEmptyPersonOid) {
        super(logger, serviceName, applicationType);

        this.allowEmptyPersonOid = allowEmptyPersonOid;
    }

    protected HakuAuditLogger(Logger logger, String serviceName, ApplicationType applicationType) {
        this(logger, serviceName, applicationType, false);
    }

    private Oid getCurrentPersonOid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String personOid = auth.getName();
        if ("anonymousUser".equals(personOid) && allowEmptyPersonOid) { // See User class in hakemus-api module
            LOGGER.debug("Allowing empty person oid");
            return null;
        }
        try {
            return new Oid(personOid);
        } catch (GSSException e) {
            throw new RuntimeException(e);
        }
    }

    public final User getUser() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Oid currentPersonOid = getCurrentPersonOid();
        Assert.notNull(sra, "Servlet request attributes not present, can not audit log all user details");
        HttpServletRequest req = sra.getRequest();
        String sessionId = req.getSession().getId();
        String useragent = req.getHeader("User-Agent");
        String remoteAddr = HttpServletRequestUtils.getRemoteAddress(req);
        try {
            InetAddress address = InetAddress.getByName(remoteAddr);
            return new User(currentPersonOid, address, sessionId, useragent);
        } catch (UnknownHostException e) {
            LOGGER.error("Error creating inetadress for user out of {}", e);
            throw new RuntimeException(e);
        }
    }
}
